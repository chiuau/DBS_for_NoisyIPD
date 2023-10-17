all: TFTI.class DBStft.class DBSpl.class DBSx.class DBSy.class DBSz.class HBSbad.class HBS.class DBSm.class DBSn.class DBSo.class DBSa.class LSF.class TFTF.class TFTTa.class GRIMa.class TMain.class

TFTI.class: TFTI.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

DBStft.class: DBStft.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

DBSpl.class: DBSpl.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

HBS.class: HBS.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

HBSbad.class: HBSbad.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

DBSa.class: DBSa.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

DBSm.class: DBSm.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

DBSn.class: DBSn.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

DBSo.class: DBSo.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

DBSx.class: DBSx.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

DBSy.class: DBSy.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

DBSz.class: DBSz.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

LSF.class: LSF.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

TFTTa.class: TFTTa.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

TFTF.class: TFTF.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

GRIMa.class: GRIMa.java
	javac $^
	cp $@ ${HOME}/My\ Documents/_IPC

TMain.class: TMain.java
	javac $^

run: TMain.class
	java TMain

jar:
	java -jar ipdlx.jar &

clean:
	rm *.class
