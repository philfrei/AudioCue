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
package src.main.java.com.adonax.audiocue.supportpack;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import src.main.java.com.adonax.audiocue.AudioCue;
import src.main.java.com.adonax.audiocue.AudioCueInstanceEvent;
import src.main.java.com.adonax.audiocue.AudioCueListener;

/**
 * {@code BattleField} is part of <em>AudioCueSupportPack</em>, 
 * a collection of classes and assets used to demonstrate the 
 * {@code AudioCue} class. {@code BattleField} illustrates an 
 * example of creating an ambient, aleatory soundscape from 
 * a minimum of assets.
 * <p>
 * The bombs and single rifle shots derive from the file 
 * gunshot.wav. The bombs are played at speeds that are much
 * slower than the original recordings, and are set to explode
 * at random times and random locations, near and far. The 
 * rifle shots are given one of two pitches and either play 
 * once or twice in succession when evoked.
 * <p>
 * The semi-automatic sfx is an edited version of the gunshot.wav
 * cue, processed in Audacity to fade to silence much more 
 * quickly than gunshot.wav. The semi-automatic is called upon 
 * to loop a random number of times when invoked, and to play
 * from one of three "locations".
 * <p>
 * This class implements {@code AudioCueListener}. Implementation 
 * methods are limited to simply reporting brief messages on the 
 * console when called.
 * <p>
 * This class makes use of the following wav files:
 * <ul><li>gunshot.wav</li>
 * <li>shortshot.wav</li></ul>
 * <p>
 * The assets should be placed in a sub-folder directly 
 * below the folder containing this file, with the sub-folder 
 * named "res".
 * <p>
 * Copies of the assets will be maintained at the following 
 * url:
 * <ul><li>https://github.com/philfrei/AudioCue/tree/master/src/main/java/com/adonax/audiocue/supportpack/res</li>
 * </ul>
 * 
 * 
 * @author Philip Freihofner
 * @version AudioCueSupportPack 1.00
 * @see http://adonax.com/AudioCue/index.html#supportpack
 */
public class BattleField implements AudioCueListener
{
	public static void main(String[] args) throws LineUnavailableException, 
		UnsupportedAudioFileException, IOException, InterruptedException 
	{
		BattleField ts = new BattleField();
		ts.battleField();
	}

	private String battleField() throws UnsupportedAudioFileException,
		IOException, LineUnavailableException, InterruptedException
	{
	    System.out.println("battleField() start");
	    
		URL url = this.getClass().getResource("res/shortshot.wav");
	    AudioCue cueSemiAuto = AudioCue.makeStereoCue(url, 3);
	    cueSemiAuto.setName("SemiAuto");
	    cueSemiAuto.addAudioCueListener(this);
	    cueSemiAuto.open();
	    /*
	     * Coding three machine guns, one close/center, and two
	     * at a distance. For the close one, we will prevent it
	     * from interrupting itself by making and managing an 
	     * instance.
	     */
		int machineGun0 = cueSemiAuto.obtainInstance();
		boolean farMachineGunPan = true;
		
		url = this.getClass().getResource("res/gunshot.wav");
	    AudioCue cueSingleShot = AudioCue.makeStereoCue(url, 8);
	    cueSingleShot.setName("SingleShotSFX");
		cueSingleShot.addAudioCueListener(this);
	    cueSingleShot.open(2048); // a little extra for the buffer
		
		long futureStop = System.currentTimeMillis() + 15_000;
		
		while (System.currentTimeMillis() < futureStop)
		{
			int switchInt = (int)(Math.random() * 8);
			
			switch(switchInt)
			{
			case 0: 
			case 1: // machine gun, nearby location
					
				// do not interrupt if already playing
				if (cueSemiAuto.getIsPlaying(machineGun0)) break;

				cueSemiAuto.setFramePosition(machineGun0, 0);
				cueSemiAuto.setLooping(machineGun0, 
						2 + (int)(Math.random() * 6));
				cueSemiAuto.setVolume(machineGun0, 0.6);
				cueSemiAuto.setSpeed(machineGun0, 1.5);
				cueSemiAuto.start(machineGun0);
				break;
				
			case 2:	
			case 3:  // two machine guns, far locations
				double pan = farMachineGunPan ? -0.6 : 0.6;
				farMachineGunPan = !farMachineGunPan;
				
				cueSemiAuto.play(0.4, pan, 1.6, 
						2 + (int)(Math.random() * 6));
				break;
				
			case 4:
			case 5:
			case 6:  // bombs and grenades
				double bvol = 0.95 - (Math.random() * 0.5);
				double bpitch = 0.1 + (Math.random() * 0.2);
				double bpan = 0.8 - Math.random() * 1.6;
				cueSingleShot.play(bvol, bpan, bpitch, 0);
				break;
				
			case 7:  // rifle, shoots once or twice
				int rifle = cueSingleShot.obtainInstance();
				if (rifle == -1) break;
				
				double gunvol = 0.7f - (Math.random() * 0.5);
				double gunpitch = 1;
				double gunpan = 0.5 - Math.random();

				if (Math.random() < 0.5) 
					cueSingleShot.setLooping(rifle, 0);
				else 
					cueSingleShot.setLooping(rifle, 1);
				
				if (Math.random() < 0.5) gunpitch = 1.2;
				
				cueSingleShot.setFramePosition(rifle, 0);
				cueSingleShot.setVolume(rifle, gunvol);
				cueSingleShot.setPan(rifle, gunpan);
				cueSingleShot.setSpeed(rifle, gunpitch);
				cueSingleShot.start(rifle);
			}
			
			Thread.sleep((int)(Math.random() * 1000) + 100);	
		}
		
		Thread.sleep(3000);
		
		cueSemiAuto.close();
		cueSingleShot.close();
		
		return "battleField() done";
	}

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
