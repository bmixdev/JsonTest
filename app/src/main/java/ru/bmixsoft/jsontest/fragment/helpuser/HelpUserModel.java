package ru.bmixsoft.jsontest.fragment.helpuser;

import ru.bmixsoft.jsontest.R;

/**
 * Created by Mike on 29.01.2018.
 */

public enum HelpUserModel {

    // создаем 3 перечисления с тайтлом и макетом
    // для удобной работы в адаптере
    Screen1(R.string.titleUserHelperScr1, R.layout.fragment_help_user_screen1),
    Screen2(R.string.titleUserHelperScr2, R.layout.fragment_help_user_screen2),
    Screen3(R.string.titleUserHelperScr3, R.layout.fragment_help_user_screen3),
    Screen4(R.string.titleUserHelperScr4, R.layout.fragment_help_user_screen4),
    Screen5(R.string.titleUserHelperScr5, R.layout.fragment_help_user_screen5),
    Screen6(R.string.titleUserHelperScr6, R.layout.fragment_help_user_screen6),
    Screen7(R.string.titleUserHelperScr7, R.layout.fragment_help_user_screen7),
    Screen8(R.string.titleUserHelperScr8, R.layout.fragment_help_user_screen8),
    Screen9(R.string.titleUserHelperScr9, R.layout.fragment_help_user_screen9),
    Screen10(R.string.titleUserHelperScr10, R.layout.fragment_help_user_screen10),
    Screen11(R.string.titleUserHelperScr11, R.layout.fragment_help_user_screen11),
    Screen12(R.string.titleUserHelperScr12, R.layout.fragment_help_user_screen12),
    Screen13(R.string.titleUserHelperScr13, R.layout.fragment_help_user_screen13),
    ScreenLast(R.string.titleFragmentDialogHelpUserScr1, R.layout.fragment_help_user_screen_last);

    private int mTitleResId;
    private int mLayoutResId;

    HelpUserModel(int title, int layout) {
        mLayoutResId = layout;
        mTitleResId = title;
    }

    public int getmTitleResId(){
        return mTitleResId;
    }

    public int getmLayoutResId()
    {
        return mLayoutResId;
    }

}
