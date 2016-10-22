package com.escherial.livingcastle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.escherial.livingcastle.LivingCastleMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 640;
		config.height = 512;

		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.maxWidth = 512;
		settings.maxHeight = 512;
		settings.filterMin = Texture.TextureFilter.Linear;
		settings.filterMag = Texture.TextureFilter.Linear;
		settings.pot=false;
		TexturePacker.process(settings, "../images", "../assets", "atlases/game");

		new LwjglApplication(new LivingCastleMain(), config);
	}
}
