package de.ottensa.talend.components.processor;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "pythonHome" }),
    @GridLayout.Row({ "pythonScript" })
})
@Documentation("TODO fill the documentation for this configuration")
public class ScriptProcessorConfiguration implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String pythonHome;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String pythonScript;

    public String getPythonHome() {
        return pythonHome;
    }

    public ScriptProcessorConfiguration setPythonHome(String pythonHome) {
        this.pythonHome = pythonHome;
        return this;
    }

    public String getPythonScript() {
        return pythonScript;
    }

    public ScriptProcessorConfiguration setPythonScript(String pythonScript) {
        this.pythonScript = pythonScript;
        return this;
    }
}