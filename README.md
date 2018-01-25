# AudioCue

**AudioCue** is a new Java resource for playing back sound files, 
designed for use with game programming. 

## Why?

Java's **Clip** class (`javax.audio.sampled.Clip`) was not designed 
with the special needs of game audio in mind. The class has a tricky, 
non-intuitive syntax and a limited feature set. A `Clip` cannot
be played concurrently with itself, can only be played at its 
recorded pitch, and the `Control` class provided for real time
changes such as panning and volume is system-dependent and 
limited by only allowing changes at buffer increments. 

**AudioCue** addresses these issues:

* ### Easy to Use
  * Very light: Download or copy/paste five class files from GitHub directly into your project.
  * Syntax is simpler than Java's `Clip` class.
  * [API](http://adonax.com/AudioCue/api) and demonstration programs provided.
* ### Powerful
  * Runs directly on Java's `SourceDataLine`.
  * Allows concurrent playback of cues.
  * Allows  playback at varying speeds.
  * Supports real-time volume, panning and frequency changes.
  * Highly configurable.
  * Includes messaging system for coordination with graphics.
* ### BSD License (open source, free, donations greatly appreciated)
* ### Now includes *AudioMixer* for consolidating AudioCues into a single output line.

<br />

_NOTE: AudioCue may not be the best choice for your project if one of the following limitations apply:_
* *AudioCue only supports one output format: 16-bit, 44100fps, stereo, little-endian (aka CD Quality).*
* *Compressed audio formats are not currently supported. As a work-around, if you are able to use another library to decompress your audio to PCM data as normalized stereo floats (data range -1 to 1, left then right), AudioCue will accept that array as input.*
* *This is not a 3D audio system. Major elements like delay-based panning, distance attenuation, Doppler effects, are not implemented.*

### Demo Jars (including source code):
[SlidersDemo](http://adonax.com/AudioCue/slidersdemo.jar) allows you three concurrent playbacks of a .wav of a bell. Sliders provided alter pitch, volume and panning in real time. A playback using Java `Clip` is provided for comparison purposes.
[FrogPondDemo](http://adonax.com/AudioCue/frogponddemo.jar) creates a soundscape of many frogs from a single croak I recorded at nearby El Cerrito Creek.
[BattleFieldDemo](http://adonax.com/AudioCue/battlefielddemo.jar) creates a soundscape of fighting in a war zone, from a wav of a single gunshot (slight cheat: the machine gun uses an Audacity edit of the same gunshot that trims the decay a bit).   

### How to get it:

[Gradle (Maven, Sbt, Leiningen)](https://jitpack.io/#philfrei/AudioCue)

- Add in your root `build.gradle` at the end of repositories:

	  allprojects {
       repositories {
           ...
           maven { url 'https://jitpack.io' }
       }
	  }

- Add the dependency

	  dependencies {
       compile 'com.github.philfrei:AudioCue:-SNAPSHOT' 
	  }

## Manual Installation

AudioCue requires five files:
* [AudioCue.java](https://github.com/philfrei/AudioCue/blob/master/src/main/java/com/adonax/audiocue/AudioCue.java)
* [AudioCueInstanceEvent.java](https://github.com/philfrei/AudioCue/blob/master/src/main/java/com/adonax/audiocue/AudioCueInstanceEvent.java)
* [AudioCueListener.java](https://github.com/philfrei/AudioCue/blob/master/src/main/java/com/adonax/audiocue/AudioCueListener.java)
* [AudioMixer.java](https://github.com/philfrei/AudioCue/blob/master/src/main/java/com/adonax/audiocue/AudioMixer.java)
* [AudioMixerTrack.java](https://github.com/philfrei/AudioCue/blob/master/src/main/java/com/adonax/audiocue/AudioMixerTrack.java)

In addition, there are two optional file folders with demo content and resources used by the demo programs:
* [supportpack](https://github.com/philfrei/AudioCue/tree/master/src/main/java/com/adonax/audiocue/supportpack)
* [supportpack.res](https://github.com/philfrei/AudioCue/tree/master/src/main/java/com/adonax/audiocue/supportpack/res)

Manual installation involves copying and pasting the five files into 
your project.

* Method 1) navigate to, then copy and paste the five files
directly into your program.
* Method 2) download [audiocue.jar](http://adonax.com/AudioCue/audiocue.jar), 
which includes source code, the "supportpack" and "res" content, 
and import into your IDE.
_[NOTE: I'm not clear if the .jar file, which I generated from Eclipse
on 11/10/2017, can be imported into other IDEs. If run, the .jar 
file executes a program that demonstrates the real time capabilities.]_

## Usage

    // Simple case example ("fire-and-forget" playback):
    // assumes sound file "myAudio.wav" exists in same file folder,
    // we will allow up to four concurrent instances.
    
    // Preparatory steps (do these prior to playback): 
    URL url = this.getClass().getResource("myAudio.wav");
    AudioCue myAudioCue = AudioCue.makeStereoCue(url, 4); //allows 4 concurrent
    myAudioCue.open();  // see API for parameters to override "sound thread" configuration defaults
    
    // For playback, normally done on demand:
    myAudioCue.play();  // see API for parameters to override default vol, pan, pitch 

    // release resources when sound is no longer needed
    myAudioCue.close();

### Usage: real time controls

An important feature of *AudioCode* is the the ability to drill down 
to individual playing instances of a cue and alter properties in real
time. To drill down to a specific instance, we can one of two methods
to capture an `int` handle that will identify the instance. The first 
is to capture the return value of the play method, as follows:

    int handle = myAudioCue.play(); 

Another way is to directly poll a handle from the pool of available 
instances, as follows:

    int handle = myAudioCue.obtainInstance(); 

An instance that is obtained in the second manner must be directly 
started, stopped, and released (returned to the pool of available 
instances). 

    myAudioCue.start(handle); // to start an instance
    myAudioCue.stop(handle);  // to stop an instance
    myAudioCue.release(handle); // to return the instance to available pool

An important distinction between an instance handle 
gotten from a `play()` method and the `obtainInstance()` method is 
that the default value of a boolean field `recycleWhenDone` differs.
An instance arising from `play()` has this value set to `true`, and an 
instance arising from `obtainInstance()` has this value set to `false`.
When an instance finishes playing, if the boolean `recycleWhenDone` is 
`true`, the instance is automatically returned to the pool of available 
instances and no longer available for updating. If the value is `false`,
properties of the instance can continue to be updated, and the 
instance can be repositioned and restarted.

Properties that can be altered for an instance include the following:

    //*volume*: 
    myAudioCue.setVolume(handle, value); // double ranging from 0 (silent)
                                         // to 1 (full volume)
    //*panning*: 
    myAudioCue.setPan(handle, value); // double ranging from -1 (full left)
                                      // to 1 (full right)
    //*speed of playback*: 
    myAudioCue.setSpeed(handle, value); // value is a factor, 
                      // multiplied against the normal playback rate, e.g.,
                      // 2 will double playback speed, 0.5 will halve it 
    //*position*:
    myAudioCue.setFramePosition(handle, frameNumber);
    myAudioCue.setMillisecondPosition(handle, int); // position in millis
    myAudioCue.setsetFractionalPosition(int, double); // position as a fraction of whole cue,
                                                    // where 0 = first frame, 1 = last frame

### Usage: output configuration

Output configuration occurs with the *AudioCue*'s `open()` method. The 
default configuration will use Java *AudioSystem*'s 
(*javax.sound.sampled.AudioSystem*) default `Mixer` and `SourceDataLine`, 
a 1024 frame buffer, and the highest available thread priority. 
(A high thread priority should not affect performance of the rest of 
an application, as the audio thread should spend the vast majority 
of its time in a blocked state.) The buffer size can be set to 
optimize the balance between latency and dropouts. For example, a 
longer cue, used to play many concurrent instances might require a 
larger buffer in order to minimize dropouts. 

You can override the output line defaults via using an alternate form
of the `open()` method. For example:

    myAudioCue.open(mixer, bufferFrames, threadPriority); // where mixer is javax.sound.sampled.Mixer
     
Each _AudioCue_ can have its own optimized configuration, and will 
be output on its own `SoureDataOutput` line, similar to the way that each 
Java `Clip` consumes an output line.  

### Usage: Outputting via _AudioMixer_
Alternatively, the output of an _AudioCue_ can be directed to an 
_AudioMixer_, which is part of this package. All inputs to an 
_AudioMixer_ are merged and sent out on a single `SourceDataLine`. 
This can be especially helpful for systems that have a limited 
number of output lines.

    myAudioCue.open(myAudioMixer); 

The _AudioMixer_ can also be configured for javax.sound.sampled.Mixer, 
buffer size (the default is 8192 frames), and thread priority. 
Any _AudioCue_ routed through an _AudioMixer_ will automatically be 
use the *AudioMixer*'s configuration properties. *AudioCue*s 
can be added or removed from the _AudioMixer_ while the _AudioMixer_
is playing. Pending track additions and removals are handled at 
the start of each iteration of the buffer. 

In the following example, we create and start an _AudioMixer_, add
an _AudioCue_ track, play the cue, then shut it all down.

    AudioMixer audioMixer = new AudioMixer();
    audioMixer.start();
    // At this point, AudioMixer will create and start a runnable and will
    // actively output 'silence' (zero values) on its SourceDataLine. 
    
    URL url = this.getClass().getResource("myAudio.wav");
    AudioCue myAudioCue = AudioCue.makeStereoCue(url, 1); 
    myAudioCue.open(mixer); 
    // The open method will handle adding the AudioCue to
    // the AudioMixer.
    
    myAudioCue.play(); 
    Thread.sleep(2000); // Allow cue to finish (assuming
                        // cue is shorter than 2 seconds)
    myAudioCue.close(); // will remove AudioCue from the mix                    
    audioMixer.stop();  // AudioMixer will stop outputting and will
                        // close the runnable in an 'orderly' manner.
                       
### Usage: Additional examples and test files
Additional examples and test files can be found in the _supportpack_
package. The *SlidersTest* demonstrates real time controls, via
GUI sliders. The *BattleField* and *FrogPond* show techniques
for building rich soundscapes from a minimum of resources, by taking
advantage of the volume, pitch, and pan parameters to make a single
cue provide the illusion of many individual entities.

These demo programs can be downloaded and run via the following jar files:
* [SlidersTest.jar](http://adonax.com/AudioCue/sliderstest.jar) showing real-time controls, alongside a Clip for comparison 
* [FrogPond.jar](http://adonax.com/AudioCue/frogpond.jar) built from a single frog.wav file
* [Battlefield.jar](http://adonax.com/AudioCue/battlefield.jar) built from two gunshot wavs


## DONATE
It would be great to hear (by email or by donation) if *AudioCue* has
been helpful. Positive feedback is very motivating, and much
appreciated!

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=TER23UW9HW2P8)

I'm happy to list links to games or game-makers using *AudioCue*.

## Contact Info

Programmer/Sound-Designer/Composer: Phil Freihofner

URL: http://adonax.com

Email: phil@adonax.com

Recommended forum: http://www.java-gaming.org/boards/java-sound-openal/16/view.html

If using StackOverflow for a question about this code, chances are highest
that I will eventually see it if you include the tag "javasound".