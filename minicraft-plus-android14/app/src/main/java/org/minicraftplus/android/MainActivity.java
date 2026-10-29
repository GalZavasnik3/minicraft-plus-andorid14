package org.minicraftplus.android;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import minicraft.android.GameSurface;
import minicraft.android.VirtualJoystick;
import minicraft.core.AndroidGameRunner;
import minicraft.core.Game;

public final class MainActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state); getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        FrameLayout root = new FrameLayout(this); GameSurface surface = new GameSurface(this); root.addView(surface, new FrameLayout.LayoutParams(-1, -1));
        int controlSize = dp(116); VirtualJoystick joystick = new VirtualJoystick(this);
        FrameLayout.LayoutParams joystickLayout = new FrameLayout.LayoutParams(controlSize, controlSize, android.view.Gravity.BOTTOM | android.view.Gravity.START); joystickLayout.setMargins(dp(18), 0, 0, dp(18)); root.addView(joystick, joystickLayout);
        root.addView(actionButton("ESC", "PAUSE", true), controlLayout(dp(48), Gravity.TOP | Gravity.START, dp(12)));
        root.addView(button(resourceId("backpack"), false), controlLayout(dp(56), android.view.Gravity.TOP | android.view.Gravity.END, dp(12)));
        root.addView(button(resourceId("steelsword"), true), controlLayout(dp(66), android.view.Gravity.BOTTOM | android.view.Gravity.END, dp(14)));
        root.addView(actionButton("Z", "CRAFT", false), controlLayout(dp(42), Gravity.CENTER_VERTICAL | Gravity.END, dp(10)));
        setContentView(root); AndroidGameRunner.start(this, surface);
    }
    private FrameLayout.LayoutParams controlLayout(int size, int gravity, int margin) { FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(size, size, gravity); layout.setMargins(margin, margin, margin, margin); return layout; }
    private ImageView button(int drawable, boolean sword) {
        ImageView view = new ImageView(this); view.setImageResource(drawable); view.setAdjustViewBounds(true); view.setAlpha(.9f); view.setContentDescription(sword ? "Attack" : "Inventory");
        view.setOnTouchListener((unused, event) -> { boolean down = event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN; boolean up = event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP; if (Game.input != null && (down || up)) { boolean active = down; Game.input.setAction(sword ? "ATTACK" : "MENU", active); if (sword) Game.input.setAction("SELECT", active); } return true; });
        return view;
    }
    private View actionButton(String label, String action, boolean escape) {
        TextView view = new TextView(this); view.setText(label); view.setGravity(Gravity.CENTER); view.setTextColor(Color.WHITE); view.setTextSize(escape ? 11 : 18); view.setTypeface(Typeface.DEFAULT_BOLD);
        GradientDrawable background = new GradientDrawable(); background.setShape(GradientDrawable.OVAL); background.setColor(Color.rgb(90, 90, 90)); background.setStroke(dp(1), Color.rgb(205, 205, 205));
        view.setBackground(background); view.setAlpha(.92f); view.setContentDescription(escape ? "Pause and menu" : "Crafting");
        view.setOnTouchListener((unused, event) -> { boolean down = event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN; boolean up = event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP; if (Game.input != null && (down || up)) { Game.input.setAction(action, down); if (escape) Game.input.setAction("EXIT", down); } return true; });
        return view;
    }
    @Override public boolean dispatchKeyEvent(KeyEvent event) { if (Game.input != null) { Game.input.onAndroidKey(event.getKeyCode(), event.getAction() == KeyEvent.ACTION_DOWN); if (event.getAction() == KeyEvent.ACTION_MULTIPLE && event.getCharacters() != null) for (char c : event.getCharacters().toCharArray()) Game.input.onCharacter(c); return true; } return super.dispatchKeyEvent(event); }
    @Override protected void onPause() { super.onPause(); if (Game.input != null) Game.input.releaseAll(); }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    private int resourceId(String name) { return getResources().getIdentifier(name, "drawable", getPackageName()); }
}
