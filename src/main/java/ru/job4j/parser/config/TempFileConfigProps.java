package ru.job4j.parser.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import ru.job4j.parser.props.TempFileCatalog;

@Component
@ConfigurationProperties("temp")
public class TempFileConfigProps {

    @NestedConfigurationProperty
    private TempFileCatalog catalog = new TempFileCatalog();

    public TempFileCatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(TempFileCatalog catalog) {
        this.catalog = catalog;
    }
}
