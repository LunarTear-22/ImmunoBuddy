package com.example.immunobubby;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

public abstract class BaseActivity extends AppCompatActivity {

    private ScrollView drawerMenu;
    private MaterialButton btnBurger;
    private ImageButton closeBtnMenu;
    private boolean isDrawerOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.background_light));
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background_light));
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        // Configura navbar e back button automaticamente
        setupNavbarButtons();
        setupBackButton(R.id.btnBack);
        setupDropdownDividerColor();
        setupExpandableCard();
        setupExpandableSearchBar(R.id.searchButton, R.id.searchBarContainer, R.id.btnCloseSearch, R.id.searchInput, R.id.allergeni_lista_root, R.id.recyclerAllergeni);

        btnBurger = findViewById(R.id.btnMenu);
        // l'ImageButton del burger nel header
        if (btnBurger != null) {
            setupBurgerMenu();
        }
        //setupBurgerMenu();

    }

    private void setupBurgerMenu() {

        drawerMenu = findViewById(R.id.drawer_layout); // il tuo ScrollView
        closeBtnMenu = findViewById(R.id.btn_close_menu); // bottone chiudi menu

        LinearLayout home = findViewById(R.id.home);
        LinearLayout account = findViewById(R.id.account);
        LinearLayout allergeni = findViewById(R.id.allergeni);
        LinearLayout sintomi = findViewById(R.id.nav_sintomi);
        LinearLayout reazioni = findViewById(R.id.nav_reazioni);
        LinearLayout farmaci = findViewById(R.id.nav_farmaci);
        LinearLayout kit = findViewById(R.id.nav_kit);
        LinearLayout qualita = findViewById(R.id.nav_qualita);
        LinearLayout percorsi = findViewById(R.id.nav_percorsi);

        if (drawerMenu == null || btnBurger == null || closeBtnMenu == null || home == null ||
                account == null || allergeni == null || sintomi == null || reazioni == null ||
                farmaci == null || kit == null || qualita == null || percorsi == null) return;

        home.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        account.setOnClickListener(v -> startActivity(new Intent(this, ImpostazioniAccountActivity.class)));
        allergeni.setOnClickListener(v -> startActivity(new Intent(this, AllergeniActivity.class)));
        farmaci.setOnClickListener(v -> startActivity(new Intent(this, FarmaciActivity.class)));
        kit.setOnClickListener(v -> startActivity(new Intent(this, KitEmergenzaActivity.class)));
        qualita.setOnClickListener(v -> startActivity(new Intent(this, QualitàAriaActivity.class)));


        // Nascondi menu inizialmente fuori dallo schermo
        drawerMenu.setTranslationX(-drawerMenu.getWidth());
        drawerMenu.setVisibility(View.GONE);

        // Apri menu
        btnBurger.setOnClickListener(v -> {
            if (!isDrawerOpen) {
                drawerMenu.setVisibility(View.VISIBLE);
                drawerMenu.animate()
                        .translationX(0)
                        .alpha(1f)
                        .setDuration(300)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
                isDrawerOpen = true;
            }
        });

        // Chiudi menu
        closeBtnMenu.setOnClickListener(v -> closeDrawer());

        // Chiudi menu toccando lo sfondo
        drawerMenu.setOnTouchListener((v, event) -> {
            if (isDrawerOpen) {
                closeDrawer();
                return true;
            }
            return false;
        });
    }

    private void closeDrawer() {
        drawerMenu.animate()
                .translationX(-drawerMenu.getWidth())
                .alpha(0f)
                .setDuration(250)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> drawerMenu.setVisibility(View.GONE))
                .start();

        // Configura navbar e back button automaticamente
        setupNavbarButtons();
        setupBackButton(R.id.btnBack);
        setupDropdownDividerColor();
        setupExpandableCard();
        setupExpandableSearchBar(R.id.searchButton, R.id.searchBarContainer, R.id.btnCloseSearch, R.id.searchInput, R.id.allergeni_lista_root, R.id.recyclerAllergeni);

        isDrawerOpen = false;
    }



    protected void setupExpandableCard() {
        ImageButton expandButton = findViewById(R.id.expandButton);
        View expandedSection = findViewById(R.id.expandedSection);

        if (expandButton == null || expandedSection == null) return;

        expandButton.setOnClickListener(v -> {
            if (expandedSection.getVisibility() == View.GONE) {
                expandedSection.setVisibility(View.VISIBLE);
                expandedSection.setAlpha(0f);
                expandedSection.setScaleY(0f);

                expandedSection.animate()
                        .alpha(1f)
                        .scaleY(1f)
                        .setDuration(300)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();

                expandButton.animate()
                        .rotation(180f)
                        .setDuration(250)
                        .withEndAction(() -> expandButton.setImageResource(R.drawable.unfold_less_24px))
                        .start();

            } else {
                expandedSection.animate()
                        .alpha(0f)
                        .scaleY(0f)
                        .setDuration(250)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .withEndAction(() -> expandedSection.setVisibility(View.GONE))
                        .start();

                expandButton.animate()
                        .rotation(0f)
                        .setDuration(250)
                        .withEndAction(() -> expandButton.setImageResource(R.drawable.unfold_more_24px))
                        .start();
            }
        });
    }

    protected void setupExpandableSearchBar(int fabId, int cardContainerId, int closeBtnId, int inputId, int rootLayoutId, int recyclerId) {
        FloatingActionButton fab = findViewById(fabId);
        MaterialCardView searchBar = findViewById(cardContainerId);
        ImageButton btnClose = findViewById(closeBtnId);
        TextInputEditText input = findViewById(inputId);
        ConstraintLayout rootLayout = findViewById(rootLayoutId);
        RecyclerView recycler = findViewById(recyclerId);

        if (fab == null || searchBar == null || btnClose == null || input == null || rootLayout == null) return;

        searchBar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                searchBar.removeOnLayoutChangeListener(this);
                searchBar.setPivotX(searchBar.getWidth());
                searchBar.setPivotY(searchBar.getHeight() / 2f);
            }
        });

        Runnable closeSearch = () -> {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(searchBar, "scaleX", 1f, 0f);
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(searchBar, "alpha", 1f, 0f);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(scaleX, fadeOut);
            set.setDuration(250);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    searchBar.setVisibility(View.GONE);
                    fab.show();
                }
            });
            set.start();

            input.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        };

        fab.setOnClickListener(v -> {
            if (searchBar.getVisibility() == View.GONE) {
                searchBar.post(() -> {
                    searchBar.setScaleX(0f);
                    searchBar.setAlpha(0f);
                    searchBar.setVisibility(View.VISIBLE);

                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(searchBar, "scaleX", 0f, 1f);
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(searchBar, "alpha", 0f, 1f);

                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(scaleX, fadeIn);
                    set.setDuration(300);
                    set.setInterpolator(new AccelerateDecelerateInterpolator());
                    set.start();

                    fab.hide();
                    input.requestFocus();
                });
            }
        });

        btnClose.setOnClickListener(v -> closeSearch.run());

        View.OnTouchListener outsideTouchListener = (v, event) -> {
            if (searchBar.getVisibility() == View.VISIBLE) {
                int[] loc = new int[2];
                searchBar.getLocationOnScreen(loc);
                float x = event.getRawX();
                float y = event.getRawY();
                if (x < loc[0] || x > loc[0] + searchBar.getWidth() ||
                        y < loc[1] || y > loc[1] + searchBar.getHeight()) {
                    closeSearch.run();
                    return true;
                }
            }
            return false;
        };

        rootLayout.setOnTouchListener(outsideTouchListener);
        recycler.setOnTouchListener(outsideTouchListener);
    }

    private void setupNavbarButtons() {
        MaterialButton btnHome = findViewById(R.id.nav_home);
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
            });
        }

        MaterialButton btnAccount = findViewById(R.id.btnAccountNav);
        if (btnAccount != null) {
            btnAccount.setOnClickListener(v -> {
                startActivity(new Intent(this, ImpostazioniAccountActivity.class));
                overridePendingTransition(0, 0);
            });
        }

        MaterialButton btnAllergeni = findViewById(R.id.btnAllergeni);
        if (btnAllergeni != null) {
            btnAllergeni.setOnClickListener(v -> {
                startActivity(new Intent(this, AllergeniActivity.class));
                overridePendingTransition(0, 0);
            });
        }
    }

    private void setupBackButton(int backButtonId) {
        View btnBack = findViewById(backButtonId);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private void setupDropdownDividerColor() {
        MaterialAutoCompleteTextView dropdown = findViewById(R.id.dropdownGender);
        if (dropdown != null) {
            dropdown.setDropDownBackgroundDrawable(
                    getResources().getDrawable(R.drawable.bg_dropdown)
            );
        }
    }
}
