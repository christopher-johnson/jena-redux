module org.apache.jena.base {
    requires slf4j.api;
    requires java.logging;
    requires commons.lang3;
    requires collection;
    exports org.apache.jena.base;
    exports org.apache.jena.base.atlas;
    exports org.apache.jena.base.atlas.logging;
    exports org.apache.jena.base.atlas.lib;
    exports org.apache.jena.base.atlas.io;
    exports org.apache.jena.base.atlas.iterator;
    exports org.apache.jena.base.atlas.lib.cache;
    exports org.apache.jena.base.atlas.lib.persistent;
    exports org.apache.jena.base.atlas.lib.tuple;
}