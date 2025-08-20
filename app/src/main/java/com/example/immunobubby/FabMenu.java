package com.example.immunobubby;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FabMenu {

    private boolean isOpen = false;
    private final FloatingActionButton fabMain;
    private final FloatingActionButton fab1;
    private final FloatingActionButton fab2;
    private final FloatingActionButton fab3;

    public FabMenu(FloatingActionButton fabMain, FloatingActionButton fab1,
                   FloatingActionButton fab2, FloatingActionButton fab3) {
        this.fabMain = fabMain;
        this.fab1 = fab1;
        this.fab2 = fab2;
        this.fab3 = fab3;

        setup();
    }

    private void setup() {
        // All'inizio nascosti
        fab1.setVisibility(View.INVISIBLE);
        fab2.setVisibility(View.INVISIBLE);
        fab3.setVisibility(View.INVISIBLE);

        fabMain.setOnClickListener(v -> toggleMenu());
    }

    private void toggleMenu() {
        if (isOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    private void openMenu() {
        showFab(fab1, -200, 0);   // sposta a sinistra
        showFab(fab2, 0, -200);   // sposta in alto
        showFab(fab3, -150, -150); // sposta in diagonale
        isOpen = true;
    }

    private void closeMenu() {
        hideFab(fab1);
        hideFab(fab2);
        hideFab(fab3);
        isOpen = false;
    }

    private void showFab(FloatingActionButton fab, float transX, float transY) {
        fab.setVisibility(View.VISIBLE);
        fab.setAlpha(0f);
        fab.setTranslationX(0f);
        fab.setTranslationY(0f);

        fab.animate()
                .translationX(transX)
                .translationY(transY)
                .alpha(1f)
                .setDuration(200)
                .setListener(null);
    }

    private void hideFab(FloatingActionButton fab) {
        fab.animate()
                .translationX(0f)
                .translationY(0f)
                .alpha(0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fab.setVisibility(View.INVISIBLE);
                    }
                });
    }
}
