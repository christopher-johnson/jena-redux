module org.apache.jena.dboe {
    requires org.apache.jena.base;
    requires org.apache.jena.core;
    requires org.apache.jena.arq;
    requires slf4j.api;
    requires org.apache.commons.lang3;
    requires java.management;
    requires libthrift;
    requires java.xml;
    exports org.apache.jena.dboe;
    exports org.apache.jena.dboe.tdb2;
    provides org.apache.jena.core.sys.JenaSubsystemLifecycle with org.apache.jena.dboe.tdb2.sys.InitTDB2;
    uses org.apache.jena.core.sys.JenaSubsystemLifecycle;
}