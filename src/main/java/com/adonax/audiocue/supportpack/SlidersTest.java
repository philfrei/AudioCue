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

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import src.main.java.com.adonax.audiocue.AudioCue;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.stage.Stage;

/**
 * {@code SlidersTest} is part of {@codeAudioCueSupportPack}, 
 * a collection of classes and assets used to demonstrate the 
 * {@code AudioCue} class. {@code SlidersTest} demonstrates 
 * the ability of an {@code AudioCue} to respond, in real time, 
 * to volume, panning and pitch controls.
 * <p>
 * The sliders send values to individual instances of a single 
 * {@code AudioCue} playing the asset file {@code a3.wav} 
 * (a bell sound pitched at MIDI A3, or 220 Hz).
 * Three instances are provided. For comparison purposes, a 
 * fourth button (second row) plays a {@code Clip} implementation 
 * of the same asset file.
 * <p>
 * The asset should be placed in a sub-folder directly 
 * below the folder containing this file, with the sub-folder 
 * named "res". A copy of the asset will be maintained at the 
 * following url:
 * <ul><li>http://adonax.com/AudioCue/a.wav</li></ul>
 * 
 * 
 * @author Philip Freihofner
 * @version AudioCueSupportPack 1.00
 * @see http://adonax.com/AudioCue/index.html#supportpack
 */
public class SlidersTest extends Application
{
	public enum Param {PITCH, VOLUME, PAN};
	
	Stage primaryStage;
	SoundHandler sh;
	
	public static void main(String[] args) 
	{
        Application.launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception 
	{	
		this.primaryStage = arg0;
	    primaryStage.setTitle("AudioCue realtime slider tests");
	    Group root = new Group();
	    Scene scene = new Scene(root, 400, 500);
	
	    sh = new SoundHandler();
	    
	    /////////////////////
	    // C O N T R O L S //
	    /////////////////////

		Button btnPlay0 = new Button("Play");
		
		btnPlay0.setOnMousePressed(e ->	sh.playAudioCue(0));
		Button btnPlay1 = new Button("Play");
		btnPlay1.setOnMousePressed(e -> sh.playAudioCue(1));
		Button btnPlay2 = new Button("Play");
		btnPlay2.setOnMousePressed(e -> sh.playAudioCue(2));
	    
		Label lbPitch0 = new Label("Frq");
		Label lbPitch1 = new Label("Frq");
		Label lbPitch2 = new Label("Frq");
		Label lbVolume0 = new Label("Vol");
		Label lbVolume1 = new Label("Vol");
		Label lbVolume2 = new Label("Vol");
		Label lbPan0 = new Label("Pan");
		Label lbPan1 = new Label("Pan");
		Label lbPan2 = new Label("Pan");

		ScrollBar sbVolume0 = new ScrollBar();
		sbVolume0.setOrientation(Orientation.VERTICAL);
		sbVolume0.setMax(128);
		sbVolume0.valueProperty().addListener(
	        (observable, oldvalue, newvalue) ->
	        {
	            sh.update(0, Param.VOLUME, 1 - newvalue.intValue() / 128f);
	        });
	
		ScrollBar sbVolume1 = new ScrollBar();
		sbVolume1.setOrientation(Orientation.VERTICAL);
		sbVolume1.setMax(128);
		sbVolume1.valueProperty().addListener(
	        (observable, oldvalue, newvalue) ->
	        {
	            sh.update(1, Param.VOLUME, 1 - newvalue.intValue() / 128f);
	        });
		
		ScrollBar sbVolume2 = new ScrollBar();
		sbVolume2.setOrientation(Orientation.VERTICAL);
		sbVolume2.setMax(128);
		sbVolume2.valueProperty().addListener(
	        (observable, oldvalue, newvalue) ->
	        {
	            sh.update(2, Param.VOLUME, 1 - newvalue.intValue() / 128f);
	        });
		
		ScrollBar sbPan0 = new ScrollBar();
		sbPan0.setOrientation(Orientation.VERTICAL);
		sbPan0.setMax(128);
		sbPan0.valueProperty().addListener(
	        (observable, oldvalue, newvalue) ->
	        {
	            sh.update(0, Param.PAN, 1 - newvalue.intValue() / 64.0);
	        });
		sbPan0.setValue(64);
	
		ScrollBar sbPan1 = new ScrollBar();
		sbPan1.setOrientation(Orientation.VERTICAL);
		sbPan1.setMax(128);
		sbPan1.valueProperty().addListener(
	        (observable, oldvalue, newvalue) ->
	        {
	            sh.update(1, Param.PAN, 1 - newvalue.intValue() / 64.0);
	        });
		sbPan1.setValue(64);
		
		ScrollBar sbPan2 = new ScrollBar();
		sbPan2.setOrientation(Orientation.VERTICAL);
		sbPan2.setMax(128);
		sbPan2.valueProperty().addListener(
	        (observable, oldvalue, newvalue) ->
	        {
	            sh.update(2, Param.PAN, 1 - newvalue.intValue() / 64.0);
	        });
		sbPan2.setValue(64);

		ScrollBar sbPitch0 = new ScrollBar();
		sbPitch0.setOrientation(Orientation.VERTICAL);
		sbPitch0.setMax(128);
		sbPitch0.valueProperty().addListener(
	        (observable, oldvalue, newvalue) ->
	        {
	            sh.update(0, Param.PITCH, 1 - newvalue.intValue() / 128f);
	        });
		sbPitch0.setValue(64);
		
		ScrollBar sbPitch1 = new ScrollBar();
		sbPitch1.setOrientation(Orientation.VERTICAL);
		sbPitch1.setMax(128);
		sbPitch1.valueProperty().addListener(
	        (observable, oldvalue, newvalue) ->
	        {
	            sh.update(1, Param.PITCH, 1 - newvalue.intValue() / 128f);
	        });
		sbPitch1.setValue(64);
		
		ScrollBar sbPitch2 = new ScrollBar();
		sbPitch2.setOrientation(Orientation.VERTICAL);
		sbPitch2.setMax(128);
		sbPitch2.valueProperty().addListener(
	        (observable, oldvalue, newvalue) ->
	        {
	            sh.update(2, Param.PITCH, 1 - newvalue.intValue() / 128f);
	        });
		sbPitch2.setValue(64);
		
	    HBox hb0 = new HBox();
	    hb0.setSpacing(10);
	    hb0.getChildren().addAll(sbPitch0, sbVolume0, sbPan0);

	    HBox hb1 = new HBox();
	    hb1.setSpacing(10);
	    hb1.getChildren().addAll(sbPitch1, sbVolume1, sbPan1);

	    HBox hb2 = new HBox();
	    hb2.setSpacing(10);
	    hb2.getChildren().addAll(sbPitch2, sbVolume2, sbPan2);

	    HBox hbL0 = new HBox();
	    hbL0.setSpacing(5);
	    hbL0.getChildren().addAll(lbPitch0, lbVolume0, lbPan0);
	    
	    HBox hbL1 = new HBox();
	    hbL1.setSpacing(5);
	    hbL1.getChildren().addAll(lbPitch1, lbVolume1, lbPan1);
	    
	    HBox hbL2 = new HBox();
	    hbL2.setSpacing(5);
	    hbL2.getChildren().addAll(lbPitch2, lbVolume2, lbPan2);

	    
		Button btnPlayClip = new Button("Play Clip");
		btnPlayClip.setOnMousePressed(e -> sh.playClip());
		
	    /***************
	     *  Layout GRID
	     ***************/
	    GridPane grid = new GridPane();
	    grid.setPadding(new Insets(10));
	    grid.setVgap(10);
	    grid.setHgap(10);
	    
	    grid.add(btnPlay0, 0, 0);
	    grid.add(btnPlay1, 1, 0);
	    grid.add(btnPlay2, 2, 0);
	    
	    grid.add(hb0, 0, 1);
	    grid.add(hb1, 1, 1);
	    grid.add(hb2, 2, 1);
	    
	    grid.add(hbL0, 0, 2);
	    grid.add(hbL1, 1, 2);
	    grid.add(hbL2, 2, 2);
	    
	    grid.add(btnPlayClip, 0, 3);
	    
	    root.getChildren().add(grid);
	    primaryStage.setScene(scene);
	    primaryStage.show();
	    
	    sh.start();        
	
	    primaryStage.setOnCloseRequest( e -> btnClose_Clicked() );
	}

	private Object btnClose_Clicked() 
	{
		sh.stop();	
		primaryStage.close();
		return null;
	}
	
	class SoundHandler
	{	
		private AudioCue cue;
		private final int POLYPHONY = 3;
		private double[] frequencies= {1, 1, 1};
		private double[] volumes = {1, 1, 1};
		private double[] pans = {0, 0, 0};
		
		private Clip clip;
		private AudioInputStream ais;
		
		SoundHandler() throws UnsupportedAudioFileException, 
			IOException, LineUnavailableException 
		{	
			URL url = this.getClass().getResource("res/a3.wav");
			
			// for the AudioCue
			cue = AudioCue.makeStereoCue(url, POLYPHONY);
			cue.setPanType(AudioCue.PanType.CIRCULAR);
			for (int i = 0; i < POLYPHONY; i++) cue.obtainInstance();
			
			// for the Clip
		    ais = AudioSystem.getAudioInputStream(url);
		    DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
		    clip = (Clip) AudioSystem.getLine(info);
		}			
			
		// *************************
		//         METHODS
		// *************************
		public void start() throws UnsupportedAudioFileException, 
			IOException, LineUnavailableException
		{				
			cue.open(1024);
		    clip.open(ais);
		}	
			
		public void stop()
		{
			cue.close();
			
			clip.stop();
			clip.flush();
			clip.close();	
		}

		// Inputs assumed normalized [0..1]
		public void update(int idx, Param param, double value)
		{
			switch (param)
			{
				case PITCH:
				{
					// GUI allows two octaves either way
					value = (float)Math.pow(2, value * 4) / 4;
					frequencies[idx] = value;
					cue.setSpeed(idx, frequencies[idx]);
					break;
				}
				case VOLUME:
				{
					volumes[idx] = value;
					cue.setVolume(idx, value);
					break;
				}
				case PAN:
				{
					pans[idx] = value;
					cue.setPan(idx,  value);
					break;
				}
			}
		}	
		
		void playAudioCue(int i)
		{
			// restart from beginning!
			cue.stop(i);
			cue.setFramePosition(i, 0);
			cue.setVolume(i, volumes[i]);
			cue.setPan(i, pans[i]);
			cue.setSpeed(i, frequencies[i]);
			cue.start(i);
		}
		
		void playClip()
		{
			clip.stop();
			clip.setFramePosition(0);
			clip.start();
		}
	}
}
