JAVAC=javac
JFLAGS=
CLASSES=cncnet/server/UDPServer.class cncnet/Client.class cncnet/Server.class

all: cncnet-server

cncnet/tools/%.class: cncnet/tools/%.java
	$(JAVAC) $(JFLAGS) $<

cncnet/%.class: cncnet/%.java
	$(JAVAC) $(JFLAGS) $<

%.class: %.java
	$(JAVAC) $(JFLAGS) $<

cncnet-server: $(CLASSES)

clean:
	rm -rf *.class cncnet/*.class cncnet/server/*.class
