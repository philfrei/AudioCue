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

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.adonax.audiocue.AudioCue;
import com.adonax.audiocue.AudioCueInstanceEvent;
import com.adonax.audiocue.AudioCueListener;

/**
 * {@code FrogPond} is part of <em>AudioCueSupportPack</em>, 
 * a collection of classes and assets used to demonstrate the 
 * {@code AudioCue} class. {@code FrogPond} illustrates an 
 * example of creating an ambient, aleatory soundscape from 
 * a minimum of assets.
 * <p>
 * All the frogs heard derive from concurrent instances of
 * a single {@code AudioCue} playing the asset "frog.wav", 
 * a frog croak. Random numbers within carefully defined 
 * ranges are used as volume, panning and frequency arguments 
 * to give the sense that there are many frogs a different 
 * angles and distances relative to the listener.
 * <p>
 * This class implements {@code AudioCueListener}. Implementation 
 * methods are limited to simply reporting brief messages on the 
 * console when called.
 * <p>
 * This class makes use of the following wav file:
 * <ul><li>frog.wav</li></ul>
 * <p>
 * The asset should be placed in a sub-folder directly 
 * below this folder, with the sub-folder named "res".
 * A copy of the asset will be maintained at the following 
 * url:
 * <ul><li>http://adonax.com/AudioCue/frog.wav</li></ul>
 * 
 * 
 * @author Philip Freihofner
 * @version AudioCueSupportPack 1.00
 * @see http://adonax.com/AudioCue/index.html#supportpack
 */
public class FrogPond implements AudioCueListener
{
	public static void main(String[] args) throws LineUnavailableException, 
		UnsupportedAudioFileException, IOException, InterruptedException 
	{
		FrogPond ts = new FrogPond();
		ts.frogPond();
	}

	private String frogPond() throws UnsupportedAudioFileException,
		IOException, LineUnavailableException, InterruptedException
	{
	    System.out.println("frogPond() start");
		URL url = this.getClass().getResource("res/frog.wav");
	    AudioCue cue = AudioCue.makeStereoCue(url, 4);
	    cue.addAudioCueListener(this);
	    cue.open();
		Thread.sleep(100);
		
		// Play for 15 seconds.
		long futureStop = System.currentTimeMillis() + 15_000;
		
		while (System.currentTimeMillis() < futureStop)
		{
			cue.play(0.3 + (Math.random() * 0.5), 
					1 - Math.random() * 2, 
					1.02 - Math.random() * 0.08,
					0);
			Thread.sleep((int)(Math.random() * 750) + 50);
		}
		
		Thread.sleep(1000);
		cue.close();
		
		return "frogPond() done";
	}
	

	/*
	 * The following methods implement AudioCueListener.
	 * 
	 * (non-Javadoc)
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

	@Override
	public void audioCueClosed(long now, AudioCue source) 
	{
		System.out.println("AudioCueListener.close for AudioCue: "
				+ source.getName() + " called at " + now 
				+ " milliseconds.");
	}
	
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
