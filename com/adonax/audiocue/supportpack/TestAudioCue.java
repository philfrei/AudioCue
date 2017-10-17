/*
 * This file is part of AudioCueSupportPack, 
 * Copyright 2017 Philip Freihofner.
 *  
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the following 
 * disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above 
 * copyright notice, this list of conditions and the following 
 * disclaimer in the documentation and/or other materials 
 * provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of 
 * its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written 
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.adonax.audiocue.supportpack;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.adonax.audiocue01.AudioCue;
import com.adonax.audiocue01.AudioCueInstanceEvent;
import com.adonax.audiocue01.AudioCueListener;

/**
 * {@code TestAudioCue} is part of <em>AudioCueSupportPack</em>,
 * a collection of classes and assets used to demonstrate the
 * {@code AudioCue} class. {@code TestAudioCue} comprises a 
 * limited panel of tests and demos, some of which perform 
 * {@code asserts} (include "-ea" as a VM argument 
 * to enable {@code asserts}), and those which require one to 
 * listen to the playback and judge correctness by ear.
 * <p>
 * In the {@code main} method, there is a series of {@code if}
 * statements, one per test. To select a test, place a <em>t</em> 
 * or the value {@code true} in the if-condition for that test. 
 * To suppress a test, put an <em>f</em> or the value {@code false}
 * in the if-condition.
 * <p>
 * This panel is neither exhaustive nor complete.
 * <p>
 * The panel makes use of four wav files:
 * <ul><li>a.wav</li>
 * <li>frog.wav</li>
 * <li>gunshot.wav</li>
 * <li>shortshot.wav</li></ul>
 * <p>
 * These assets should be placed in a sub-folder directly 
 * below this folder, with the sub-folder named "res".
 * Copies of the assets will be maintained at the following 
 * urls:
 * <ul><li> http://adonax.com/AudioCue/a.wav</li>
 * <li> http://adonax.com/AudioCue/frog.wav</li>
 * <li> http://adonax.com/AudioCue/gunshot.wav</li>
 * <li> http://adonax.com/AudioCue/shortshot.wav</li></ul>
 * 
 * @author Philip Freihofner
 * @version AudioCueSupportPack 1.1
 * @see http://adonax.com/AudioCue/index.html#supportpack
 */
public class TestAudioCue implements AudioCueListener
{
	public static void main(String[] args) throws LineUnavailableException, 
		UnsupportedAudioFileException, IOException, InterruptedException 
	{
		TestAudioCue ts = new TestAudioCue();
		final boolean f = false; if (f);
		final boolean t = true; if (t);
		
		// place a t (true) to run, an f (false) to skip
		if (t) ts.runTest(ts.verifyClip());
		if (t) ts.runTest(ts.testOpenClose());
		if (t) ts.runTest(ts.checkGetLengthMethods());
		if (t) ts.runTest(ts.testLoadFromArray());
		if (t) ts.runTest(ts.testConcurrentPlays());
		if (t) ts.runTest(ts.testPlaybackSpeed());
		if (t) ts.runTest(ts.testReset());
		if (t) ts.runTest(ts.testLooping());
	}

	private void runTest(String s) 
	{
		System.out.println(s + "\n");
	}

	/*
	 * Verifies that the audio file can be read by a Clip.
	 */
	private String verifyClip() throws UnsupportedAudioFileException, 
		IOException, LineUnavailableException, InterruptedException
	{	
		System.out.println("verifyClip() start");
		URL url;
//		url = this.getClass().getResource("res/gunshot.wav");
		url = this.getClass().getResource("res/a3.wav");
//		url = this.getClass().getResource("res/frog.wav");
		
		System.out.println("Test Normal Java Clip");
	    AudioInputStream ais = AudioSystem.getAudioInputStream(url);
	    DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
	    Clip clip = (Clip) AudioSystem.getLine(info);
	    clip.open(ais);
	    
	    System.out.println("Clip length in frames:" + clip.getFrameLength());
	    System.out.println("Clip length in microseconds:" +
	    		clip.getMicrosecondLength());
	    
		Thread.sleep(100);
	    // The "play" of the normal Java Clip
		clip.start();
	    Thread.sleep(3000);
	    clip.close();
	    
	    return "verifyClip() finished";
	}
	 
	private String testOpenClose() throws InterruptedException,
		LineUnavailableException, UnsupportedAudioFileException, 
		IOException 
	{
	    System.out.println("testOpenClose() start");
		
	    boolean openTestSucceeds = false;
	    boolean closeTestSucceeds = false;
	    
	    URL url = this.getClass().getResource("res/a3.wav");	    
	    AudioCue cue = AudioCue.makeStereoCue(url, 1); 
	    cue.addAudioCueListener(this);
	    
	    cue.open();
		Thread.sleep(100);

	    try { cue.open(); }
	    catch (IllegalStateException e) 
	    {
	    	openTestSucceeds = true;
	    }
		Thread.sleep(100);
		cue.close();
		
		Thread.sleep(100);
		try	{ cue.close(); }
		catch (IllegalStateException e)
		{
			closeTestSucceeds = true;
		}
		
		assert (openTestSucceeds);
		assert (closeTestSucceeds);
		System.out.println("asserts passed");
		
		return "testOpenClose() done";
	}

	private String checkGetLengthMethods() throws 
		UnsupportedAudioFileException, IOException, 
		LineUnavailableException
	{
		System.out.println("checkGetLengthMethods() start");
		
		// wav stats via Clip methods
		URL url = this.getClass().getResource("res/a3.wav");
		AudioInputStream ais = AudioSystem.getAudioInputStream(url);
	    DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
	    Clip clip = (Clip) AudioSystem.getLine(info);
	    clip.open(ais);
	    long clipFrames = clip.getFrameLength();
	    long clipMicroseconds = clip.getMicrosecondLength();
	    System.out.println("Clip length in frames:" + clipFrames);
	    System.out.println("Clip length in microseconds:" +
	    		clipMicroseconds);
		clip.close();
		
		// wav stats via AudioCue methods
		AudioCue cue = AudioCue.makeStereoCue(url, 1);
		long cueFrames = cue.getFrameLength();
	    long cueMicroseconds = cue.getMicrosecondLength();
	    
	    System.out.println("Cue length in frames:" + cueFrames);
	    System.out.println("Cue length in microseconds:"
	    		+ cueMicroseconds);
	    
	    assert (cueFrames == clipFrames);
	    assert (cueMicroseconds == clipMicroseconds);
	    
		
		return "checkGetLengthMethods() done";
	}
	
	
	private String testLoadFromArray() throws 
		UnsupportedAudioFileException, IOException, 
		LineUnavailableException, InterruptedException
	{
		System.out.println("testLoadFromArray() start");
		
		float[] data = new float[44100 * 2];
		
		for (int i = 0; i < 44100; i++)
		{
			data[2 * i] = (float)(Math.sin(i * 0.015625));
			data[2 * i + 1] = data[2 * i];
		}
		
		// wav stats via AudioCue methods
		AudioCue cue = AudioCue.makeStereoCue(data, "sine", 1);
	    System.out.println("Cue length in frames:" 
	    		+ cue.getFrameLength());
	    System.out.println("Cue length in microseconds:"
	    		+ cue.getMicrosecondLength());
	    
	    cue.addAudioCueListener(this);
	    cue.open();
	    
	    cue.play(0.75);
	    Thread.sleep(2000);
	    
	    cue.close();
	    
		
		return "testLoadFromArray() done";
	}
	
	
	private String testConcurrentPlays() throws 
		UnsupportedAudioFileException, IOException, 
		LineUnavailableException, InterruptedException
	{
	    System.out.println("testConcurrentPlays() start");
		URL url = this.getClass().getResource("res/a3.wav");	    
	    AudioCue cue = AudioCue.makeStereoCue(url, 4); // one less than played!
	    cue.addAudioCueListener(this);
	    cue.open();
		Thread.sleep(100);

	    PlayFromAnotherThread ot = new PlayFromAnotherThread();
	    ot.audioCue = cue;
	    Thread t = new Thread(ot);
	    t.start();
		
	    int a0 = cue.play(0.8);
	    if (a0 >= 0) System.out.println(">Main thread instance played:" + a0);
		Thread.sleep(750);
		int a1 = cue.play(0.8, 0, 1.1, 0);
	    if (a1 >= 0) System.out.println(">Main thread instance played:" + a1);
		Thread.sleep(250);
		int a2 = cue.play(0.8, 0, 1.2, 0);
	    if (a2 >= 0) System.out.println(">Main thread instance played:" + a2);
		
		Thread.sleep(6000);
		cue.close();

		return "testConcurrentPlays() done";
	}

	class PlayFromAnotherThread implements Runnable
	{
		AudioCue audioCue;
		
		@Override
		public void run() 
		{
			try {Thread.sleep(500);} 
			catch (InterruptedException e1) {e1.printStackTrace();}
			
			int b0 = audioCue.play(0.8, 0, 0.95, 0);
			if (b0 >= 0) System.out.println(">2nd thread instance played:" + b0);
			
			try {Thread.sleep(500);} 
			catch (InterruptedException e1) {e1.printStackTrace();}
			
			int b1 = audioCue.play(0.8, 0, 0.9, 0);
			if (b1 >= 0) System.out.println(">2nd thread instance played:" + b1);
		}
	}
	
	private String testPlaybackSpeed() throws 
		UnsupportedAudioFileException, IOException, 
		LineUnavailableException, InterruptedException
	{
	    System.out.println("testPlaybackSpeed() start");
		URL url = this.getClass().getResource("res/gunshot.wav");
//		URL url = this.getClass().getResource("res/a3.wav");	    
	    AudioCue cue = AudioCue.makeStereoCue(url, 4);
	    cue.addAudioCueListener(this);
		cue.open();
		Thread.sleep(100);
		
		System.out.println(">default play speed");
		cue.play(0.8);
		Thread.sleep(1000);
		System.out.println(">play speed = 1");
		cue.play(0.8, 0, 1, 0);
		Thread.sleep(1000);

		System.out.println(">play speed = 2.5");
		cue.play(0.8, 0, 2.5, 0);
		Thread.sleep(1000);
		
		System.out.println(">play speed = 0.15");
		cue.play(0.8, 0, 0.15, 0);
		Thread.sleep(4000);
		cue.close();
	
		return "testPlaybackSpeed() done";
	}
	
	private String testReset() throws UnsupportedAudioFileException,
		IOException, LineUnavailableException, InterruptedException
	{
	    System.out.println("testReset() start");
		URL url = this.getClass().getResource("res/gunshot.wav");
	    AudioCue cue = AudioCue.makeStereoCue(url, 2);
	    cue.addAudioCueListener(this);
	    cue.open();
		Thread.sleep(100);
		
		long frameLength = cue.getFrameLength();
		
		System.out.println(cue.getName() + " has "  
				+ frameLength + " frames.");
		
		
		int acc = cue.play(0.9, 0, 0.25, 0);
		cue.setRecycleWhenDone(acc, false);
		Thread.sleep(1000);
		
		System.out.println(">test fractional position back to 0");
		cue.stop(acc);
		cue.setFractionalPosition(acc, 0);
		cue.start(acc);
		Thread.sleep(1000);
		
		System.out.println(">test fractional position to 0.25");
		cue.stop(acc);
		cue.setFractionalPosition(acc, 0.25);
		assert (cue.getFramePosition(acc) == (frameLength - 1) / 4.0);
		cue.start(acc);
		Thread.sleep(1000);
		
		System.out.println(">millisecond position to 300 millis");
		cue.stop(acc);
		cue.setMillisecondPosition(acc, 300);
		assert (cue.getFramePosition(acc) == 4410 * 3);
		cue.start(acc);
		Thread.sleep(3000);
		
		System.out.println(">setting position to absurdly high value");
		cue.stop(acc);
		cue.setFramePosition(acc, 44100 * 2000); // cue is NOT this long
		assert (cue.getFramePosition(acc) == frameLength - 1);
		cue.setFractionalPosition(acc, 1.5);
		assert (cue.getFramePosition(acc) == frameLength - 1);
		cue.setMillisecondPosition(acc, 100_000_000);
		assert (cue.getFramePosition(acc) == frameLength - 1);
		
		System.out.println("asserts passed");		
		cue.close();
	
		return "testReset() done";
	}

	private String testLooping() throws UnsupportedAudioFileException,
	IOException, LineUnavailableException, InterruptedException
	{
	    System.out.println("testLooping() start");
		URL url = this.getClass().getResource("res/shortshot.wav");
	    AudioCue cue = AudioCue.makeStereoCue(url, 1);
	    cue.addAudioCueListener(this);
	    cue.open();
		
		Thread.sleep(100);
			
		int cc = cue.obtainInstance();
		
		cue.setLooping(cc, 3);
		cue.setVolume(cc, 0.8);
		cue.setSpeed(cc, 1.25);
		cue.start(cc);
		Thread.sleep(3000);
		cue.stop(cc);
		
		System.out.println(">test infinite loop setting for 5 seconds");
		cue.setFramePosition(cc, 0);
		cue.setLooping(cc, -1);
		cue.setVolume(cc, 0.8);
		cue.setSpeed(cc, 2.75);
		cue.start(cc);
		Thread.sleep(5000);
		cue.setLooping(cc, 0);
		Thread.sleep(200);
		
		cue.close();
		
		return "testLooping() done";
	}

	/*
	 * The following methods implement AudioCueListener.
	 * 
	 * @see com.adonax.audiocue.AudioCueListener#audioCueOpened(long, int, int, com.adonax.audiocue.AudioCue)
	 */
	@Override
	public void audioCueOpened(long now, int threadPriority, int bufferSize,
			AudioCue source) 
	{
		System.out.println("AudioCueListener.open for AudioCue: "
				+ source.getName() + " called at " + now 
				+ " milliseconds.");
		System.out.println("\tCue length = " + source.getFrameLength());
		System.out.println("\tThread priority = " + threadPriority);
		System.out.println("\tBuffer size = " + bufferSize);
	}

	/*
	 * (non-Javadoc)
	 * @see com.adonax.audiocue.AudioCueListener#audioCueClosed(long, com.adonax.audiocue.AudioCue)
	 */
	@Override
	public void audioCueClosed(long now, AudioCue source) 
	{
		System.out.println("AudioCueListener.close for AudioCue: "
				+ source.getName() + " called at " + now 
				+ " milliseconds.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.adonax.audiocue.AudioCueListener#instanceEventOccurred(com.adonax.audiocue.AudioCueInstanceEvent)
	 */
	@Override
	public void instanceEventOccurred(
			AudioCueInstanceEvent event)
	{
		switch (event.type)
		{
		case OBTAIN_INSTANCE:
			System.out.println("AudioCueListener.instanceEventOccurred "
					+ "called at " + event.time + " milliseconds.");	
			System.out.println("\tObtainInstance called for "
					+ event.source.getName() + ", instance #: " 
					+ event.instanceID);
			break;
		case LOOP:
			System.out.println("AudioCueListener.instanceEventOccurred "
					+ "called at " + event.time + " milliseconds.");	
			System.out.println("\tLoop called for " 
					+ event.source.getName() 
					+ " instance #: " + event.instanceID);
			break;
		case RELEASE_INSTANCE:
			System.out.println("AudioCueListener.instanceEventOccurred "
					+ "called at " + event.time + " milliseconds.");	
			System.out.println("\tReleaseInstance called for instance #: " 
					+ event.instanceID);	
			break;
		case START_INSTANCE:
			System.out.println("AudioCueListener.instanceEventOccurred "
					+ "called at " + event.time + " milliseconds.");	
			System.out.println("\tStart called at sample frame: " 
					+ event.frame);
			System.out.println("\tStart called for " 
					+ event.source.getName() 
					+ " instance #: " + event.instanceID);
				break;
		case STOP_INSTANCE:
			System.out.println("AudioCueListener.instanceEventOccurred "
					+ "called at " + event.time + " milliseconds.");
			System.out.println("\tStop called at sample frame: " 
					+ event.frame);
			System.out.println("\tStop called for " 
					+ event.source.getName() 
					+ " instance #: " + event.instanceID);
			break;
		default:
			break;
		}
	}
}
