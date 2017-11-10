# Beispiel Projekt Refactoring Plugin

Dies ist Beispiel Code für die Implementierung eines Refactoring Plugins für NaturalOne. Der Beispielcode ist nicht geeignet für den produktiven Einsatz.

## Voraussetzungen

Für den Build des Projects ist zunächst die ein lokales P2 Repository zu erstellen. Auszuführen im Installationsordner der Eclipse IDE for Committers. Anzupassen ist der Pfad zur Installation von NaturalOne:

java -jar ./plugins/org.eclipse.equinox.launcher_1.4.0.v20161219-1356.jar -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher -metadataRepository file:c:/p2-repository/com-softwareag-p2/ -artifactRepository file:c:/p2-repository/com-softwareag-p2/ -source c:/SoftwareAG/Designer/eclipse/ -configs gtk.linux.x86 -compress -publishArtifacts

In der Datei net.aokv.naturalone.target.target (Eclipse Target) ist der Ordner c:/p2-repository/com-softwareag-p2/ referenziert.
