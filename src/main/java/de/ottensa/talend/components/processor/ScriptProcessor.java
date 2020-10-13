package de.ottensa.talend.components.processor;

import static org.talend.sdk.component.api.component.Icon.IconType.CUSTOM;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import de.ottensa.talend.components.util.Converter;
import jep.Interpreter;
import jep.MainInterpreter;
import jep.PyConfig;
import jep.SubInterpreter;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Output;
import org.talend.sdk.component.api.processor.OutputEmitter;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import de.ottensa.talend.components.service.SeverusSnakeService;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = CUSTOM, custom = "Script") // icon is located at src/main/resources/icons/Script.svg
@Processor(name = "Script")
@Documentation("TODO fill the documentation for this processor")
public class ScriptProcessor implements Serializable {
    private final ScriptProcessorConfiguration configuration;
    private final SeverusSnakeService service;

    private Converter converter = null;

    private Interpreter interpreter;

    public ScriptProcessor(@Option("configuration") final ScriptProcessorConfiguration configuration,
                          final SeverusSnakeService service, final RecordBuilderFactory builderFactory) {
        this.configuration = configuration;
        this.service = service;

        this.converter = new Converter(builderFactory);
    }

    @PostConstruct
    public void init() throws Exception {
        // this method will be executed once for the whole component execution,
        // this is where you can establish a connection for instance
        // Note: if you don't need it you can delete it

        PyConfig cfg = new PyConfig();
        cfg.setPythonHome(configuration.getPythonHome());
        MainInterpreter.setInitParams(cfg);
    }

    @BeforeGroup
    public void beforeGroup() throws Exception {
        // if the environment supports chunking this method is called at the beginning if a chunk
        // it can be used to start a local transaction specific to the backend you use
        // Note: if you don't need it you can delete it

        interpreter = new SubInterpreter();
    }

    @ElementListener
    public void onNext(
            @Input final Record defaultInput,
            @Output final OutputEmitter<Record> defaultOutput) throws Exception {
        // this is the method allowing you to handle the input(s) and emit the output(s)
        // after some custom logic you put here, to send a value to next element you can use an
        // output parameter and call emit(value).

        // Todo: convert Record/JsonObject to nested HashMap
        Map<String, Object> in = converter.recordToMap(defaultInput);
        interpreter.set("input", in);
        interpreter.set("output", new LinkedHashMap<String, Object>());

        interpreter.runScript(configuration.getPythonScript());

        Map output = interpreter.getValue("output", Map.class);
        Record out = converter.mapToRecord(output);

        // Todo: convert HashMap to JsonObject/Record
        defaultOutput.emit(out);
    }

    @AfterGroup
    public void afterGroup() throws Exception {
        // symmetric method of the beforeGroup() executed after the chunk processing
        // Note: if you don't need it you can delete it

        if (interpreter != null) {
            interpreter.close();
        }
    }

    @PreDestroy
    public void release() throws Exception {
        // this is the symmetric method of the init() one,
        // release potential connections you created or data you cached
        // Note: if you don't need it you can delete it

        interpreter.close();
    }
}