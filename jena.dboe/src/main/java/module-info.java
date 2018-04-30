module org.apache.jena.dboe {
    requires org.apache.jena.base;
    requires org.apache.jena.core;
    requires org.apache.jena.arq;
    requires slf4j.api;
    requires commons.lang3;
    requires java.management;
    requires libthrift;
    requires java.xml;
    exports org.apache.jena.dboe;
    exports org.apache.jena.dboe.tdb2;
}