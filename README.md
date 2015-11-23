# NEOE OPUS
NeoeOpus is a Java application encodes and plays opus stream.


[Opus is audio codec thought to be better than MP3 or AAC.] (https://www.opus-codec.org/)


NeoeOpus use JNA to call libopus 1.1 (libopus-0.dll copies from opusfile-0.6-win32 from opus-codec.org) for codec opus.


NeoeOpus opens a Java swing window, when you drag-and-drop a .wav file into it, it will encode the .wav file into .neoe.opus file.
When you drop .neoe.opus file into it, it will be played.


Only works on Windows 32/64bit with 32bit JRE. Porting to linux and 64bit JRE is wellcomed.


.neoe.opus is a simple container(compared to Ogg) to contain opus stream, created by neoe.


NeoeOpus also can be used as a java library to play audio in java.


NeoeOpus do not have a playlist or other features a player should have. Improvement is wellcomed.


`Neoe`
