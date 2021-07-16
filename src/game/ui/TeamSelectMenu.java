package game.ui;

import engine.audio.AudioMaster;
import engine.audio.SoundEffect;
import engine.graphics.Material;
import engine.graphics.mesh.UiBuilder;
import engine.io.Input;
import engine.math.Mathf;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import engine.objects.GameObject;
import main.Main;
import net.ClientHandler;
import net.packets.client.TeamSelectPacket;

import java.util.Random;

import static game.ui.UserInterface.*;

public class TeamSelectMenu extends Menu {

    private UiPanel redPanel, bluePanel;
    private GameObject redTeam, blueTeam, randomTeam;
    private float redSize = 1, blueSize = 1, randomSize = 1;
    private boolean mouseOverRed = false, mouseOverBlue = false, mouseOverRandom = false;
    private int team;
    private boolean teamSelected, hasSentTeamPacket = false;
    private float selectedSize = 0.00001f, clickAnimation;


    @Override
    public void init() {
        addObject(new UiPanel(0, 0, 2, 2, 10, 0.2f));
        addObject(new UiPanel(0, 0, 2, 0.2f, 9, 0.1f));
        addObject(new UiPanel(0, 2 - 0.2f, 2, 2, 9, 0.1f));
        addObject(new GameObject(screen(1, 0.1f, 8), UiBuilder.UICenterUV(p(512f), 1, 0.125f, Material.UI_TEAM_SELECT, new Vector2f(0, 0.5f), new Vector2f(1, 0.625f))));

        redPanel = (UiPanel) addObject(new UiPanel(0, 0.2f + p(4), 1, 2 - 0.2f - p(4), 7, new Vector4f(0.5f, 0.1f, 0.05f, 0.9f)));
        bluePanel = (UiPanel) addObject(new UiPanel(1, 0.2f + p(4), 2, 2 - 0.2f - p(4), 7, new Vector4f(0.05f, 0.1f, 0.5f, 0.9f)));

        redTeam = addObject(new GameObject(screen(p(4), 0.8f, 6), UiBuilder.UIOriginUV(p(512f), 1, 0.25f, 0, 0, Material.UI_TEAM_SELECT, new Vector2f(0, 0), new Vector2f(1, 0.25f))));
        blueTeam = addObject(new GameObject(screen(2 - p(4), 0.8f, 6), UiBuilder.UIOriginUV(p(512f), 1, 0.25f, 1, 0, Material.UI_TEAM_SELECT, new Vector2f(0, 0.25f), new Vector2f(1, 0.5f))));

        randomTeam = addObject(new GameObject(screen(1, 1.9f, 6), UiBuilder.UICenterUV(p(512f), 1, 0.125f, Material.UI_TEAM_SELECT, new Vector2f(0, 0.625f), new Vector2f(1, 0.75f))));
    }

    @Override
    public void update() {
        if (!teamSelected) {
            if (getNormMouseY() > 0.2f && getNormMouseY() < 2 - 0.2f) {
                mouseOverRandom = false;
                if (getNormMouseX() < 1.0f) {
                    mouseOverBlue = false;
                    if (!mouseOverRed) {
                        AudioMaster.playSound(SoundEffect.PICKER);
                    }
                    mouseOverRed = true;
                } else {
                    mouseOverRed = false;
                    if (!mouseOverBlue) {
                        AudioMaster.playSound(SoundEffect.PICKER);
                    }
                    mouseOverBlue = true;
                }

            } else if (getNormMouseY() > 2 - 0.2f) {
                if (getNormMouseX() > 0.5f && getNormMouseX() < 1.5f) {
                    mouseOverRed = false;
                    mouseOverBlue = false;
                    if (!mouseOverRandom) {
                        AudioMaster.playSound(SoundEffect.PICKER);
                    }
                    mouseOverRandom = true;
                } else {
                    mouseOverRed = false;
                    mouseOverBlue = false;
                    mouseOverRandom = false;
                }
            } else {
                mouseOverRed = false;
                mouseOverBlue = false;
                mouseOverRandom = false;
            }

            redSize = Mathf.lerpdt(redSize, (mouseOverRed ? 1.0f : 0.0f) - (mouseOverBlue ? 1.0f : 0.0f), 0.01f);
            blueSize = Mathf.lerpdt(blueSize, (mouseOverBlue ? 1.0f : 0.0f) - (mouseOverRed ? 1.0f : 0.0f), 0.01f);
            randomSize = Mathf.lerpdt(randomSize, (mouseOverRandom ? 1.0f : -1.0f), 0.01f);

            redPanel.resize(0, 0.2f + p(4), 1 + redSize * 0.1f, 2 - 0.2f - p(4));
            bluePanel.resize(1 - blueSize * 0.1f, 0.2f + p(4), 2, 2 - 0.2f - p(4));
            redPanel.setColor(new Vector4f(0.5f + redSize * 0.05f, 0.1f + redSize * 0.05f, 0.05f + redSize * 0.05f, 0.9f));
            bluePanel.setColor(new Vector4f(0.05f + blueSize * 0.05f, 0.1f + blueSize * 0.05f, 0.5f + blueSize * 0.05f, 0.9f));

            redTeam.setPosition(screen(p(4) + p(16) * (1 + redSize * 0.5f), 0.8f, 6));
            blueTeam.setPosition(screen(2 - p(4) - p(16) * (1 + blueSize * 0.5f), 0.8f, 6));

            redTeam.setScale(Vector3f.one().times(1.1f + redSize * 0.2f));
            blueTeam.setScale(Vector3f.one().times(1.1f + blueSize * 0.2f));
            randomTeam.setScale(Vector3f.one().times(1.1f + randomSize * 0.1f));
            if (((mouseOverRed || mouseOverRandom) || mouseOverBlue) && Input.isMouseButtonDown(0)) {
                AudioMaster.playSound(SoundEffect.SELECT);
                teamSelected = true;
                if (mouseOverRed) {
                    team = 0;
                } else if (mouseOverBlue) {
                    team = 1;
                } else if (mouseOverRandom) {
                    team = new Random().nextInt(2);
                }

                if (team == 0) {
                    redPanel.setColor(new Vector4f(1, 1, 1, 0.9f));
                    redTeam.setScale(Vector3f.one().times(1.32f));
                } else if (team == 1) {
                    bluePanel.setColor(new Vector4f(1, 1, 1, 0.9f));
                    blueTeam.setScale(Vector3f.one().times(1.32f));
                }
                clickAnimation = 1;
            }
        } else {
            clickAnimation = Mathf.lerpdt(clickAnimation, 0, 100f);
            selectedSize *= (float) Math.max(Math.pow(1.08f, (Main.getDeltaTime() * 120)), 1);
            if (selectedSize > 200) {
                if (!hasSentTeamPacket) {
                    hasSentTeamPacket = true;
                    ClientHandler.client.sendTCP(new TeamSelectPacket(team));
                }
                selectedSize = 200;
            }
            if (team == 0) {
                redPanel.setColor(new Vector4f(0.5f + clickAnimation * 0.5f, 0.1f + clickAnimation * 0.9f, 0.05f + clickAnimation * 0.95f, 0.9f));

                redPanel.resize(0,
                        0.2f + p(4) - clickAnimation * 0.1f,
                        1.1f + selectedSize + clickAnimation * 0.1f,
                        2 - 0.2f - p(4) + clickAnimation * 0.1f);
                bluePanel.resize(1.1f + selectedSize + clickAnimation * 0.1f,
                        0.2f + p(4),
                        2,
                        2 - 0.2f - p(4));

                redTeam.setScale(Vector3f.lerpdt(redTeam.getScale(), Vector3f.one().times(1.3f), 10));
                blueTeam.setScale(Vector3f.lerpdt(blueTeam.getScale(), Vector3f.one().times(0.9f), 10));

                redTeam.setPosition(screen(p(28) + selectedSize * 0.1f, 0.8f, 6));
                blueTeam.setPosition(screen(2 - p(12) + selectedSize * 10, 0.8f, 6));

            } else if (team == 1) {
                bluePanel.setColor(new Vector4f(0.05f + clickAnimation * 0.95f, 0.1f + clickAnimation * 0.9f, 0.5f + clickAnimation * 0.5f, 0.9f));

                redPanel.resize(0,
                        0.2f + p(4),
                        0.9f - selectedSize - clickAnimation * 0.1f,
                        2 - 0.2f - p(4));
                bluePanel.resize(0.9f - selectedSize - clickAnimation * 0.1f,
                        0.2f + p(4) - clickAnimation * 0.1f,
                        2,
                        2 - 0.2f - p(4) + clickAnimation * 0.1f);

                redTeam.setScale(Vector3f.lerpdt(redTeam.getScale(), Vector3f.one().times(0.9f), 10));
                blueTeam.setScale(Vector3f.lerpdt(blueTeam.getScale(), Vector3f.one().times(1.3f), 10));

                redTeam.setPosition(screen(p(12) - selectedSize * 10, 0.8f, 6));
                blueTeam.setPosition(screen(2 - p(28) - selectedSize * 0.1f, 0.8f, 6));

            }
        }
    }

    @Override
    public void unload() {

    }
}
