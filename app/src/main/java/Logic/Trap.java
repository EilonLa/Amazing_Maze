package Logic;

import com.example.cdv.amazingmaze.R;

/**
 * Created by אילון on 30/01/2017.
 */

public class Trap {
    public static final int FIRE = 0;
    public static final int FIVE_STEPS = 1;
    public static final int TEN_STEPS = 2;
    public static final int TWENTY_STEPS = 3;
    public static final int FORTY_STEPS = 4;

    public static final int COST_FOR_5_STEP_SHIELD = 5;
    public static final int COST_FOR_10_STEP_SHIELD = 10;
    public static final int COST_FOR_20_STEP_SHIELD = 20;
    public static final int COST_FOR_40_STEP_SHIELD = 40;
    public static final int COST_FOR_FIREBALL = 30;

    public static int[] mCosts =
            {COST_FOR_FIREBALL, COST_FOR_5_STEP_SHIELD, COST_FOR_10_STEP_SHIELD, COST_FOR_20_STEP_SHIELD, COST_FOR_40_STEP_SHIELD};
    private static int[] mIcons =
            {R.mipmap.fireball, R.mipmap.shield_5_steps, R.mipmap.shield_10_steps, R.mipmap.shield_20_steps, R.mipmap.shield_40_steps};
    private final String[] mDescriptions =
            {"Set the path on fire (" + COST_FOR_FIREBALL + " COINS)", "Protects you from attacks for 5 steps (" + COST_FOR_5_STEP_SHIELD + " COINS)", "Protects you from attacks for 10 steps (" + COST_FOR_10_STEP_SHIELD + " COINS)", "Protects you from attacks for 20 steps (" + COST_FOR_20_STEP_SHIELD + " COINS)", "Protects you from attacks for 40 steps (" + COST_FOR_40_STEP_SHIELD + " COINS)"};
    private final String[] mNames =
            {"Fire", "Shield (5 steps)", "Shield (10 steps)", "Shield (20 steps)", "Shield (40 steps)"};

    private Tile mTile;
    private int mIconId;
    private boolean mIsAttack;
    private String mDescription;
    private String mName;
    private int mPrice;
    private int mTrapIndex;


    public Trap(int trapsIndex) {
        if (trapsIndex > 0) {
            mIsAttack = false;
        }
        if (trapsIndex == 0)
            mIsAttack = true;
        this.mTrapIndex = trapsIndex;
        if (trapsIndex < mIcons.length) {
            this.mIconId = mIcons[trapsIndex];
            this.mDescription = mDescriptions[trapsIndex];
            this.mName = mNames[trapsIndex];
            this.mPrice = mCosts[trapsIndex];
        }
    }

    public int GetPrice() {
        return mPrice;
    }

    public void SetPrice(int price) {
        this.mPrice = price;
    }

    public int GetIconResource() {
        return mIcons[mIconId];
    }

    public int GetTrapIndex() {
        return mTrapIndex;
    }


    public void SetTrapIndex(int trapIndex) {
        this.mTrapIndex = trapIndex;
    }


    public int[] GetIcons() {
        return mIcons;
    }

    public Tile GetTile() {
        return mTile;
    }

    public void SetTile(Tile tile) {
        this.mTile = tile;
    }

    public int GetIconId() {
        return mIconId;
    }

    public void SetIconId(int iconId) {
        this.mIconId = iconId;
    }

    public boolean IsAttack() {
        return mIsAttack;
    }

    public void SetAttack(boolean attack) {
        mIsAttack = attack;
    }

    public String GetDescription() {
        return mDescription;
    }

    public void SetDescription(String description) {
        this.mDescription = description;
    }

    public String GetName() {
        return mName;
    }

    public void SetName(String name) {
        this.mName = name;
    }

    public static int getFIRE() {
        return FIRE;
    }

    public static int getFiveSteps() {
        return FIVE_STEPS;
    }

    public static int getTenSteps() {
        return TEN_STEPS;
    }

    public static int getTwentySteps() {
        return TWENTY_STEPS;
    }

    public static int getFortySteps() {
        return FORTY_STEPS;
    }

    public static int getCostFor5StepShield() {
        return COST_FOR_5_STEP_SHIELD;
    }

    public static int getCostFor10StepShield() {
        return COST_FOR_10_STEP_SHIELD;
    }

    public static int getCostFor20StepShield() {
        return COST_FOR_20_STEP_SHIELD;
    }

    public static int getCostFor40StepShield() {
        return COST_FOR_40_STEP_SHIELD;
    }

    public static int getCostForFireball() {
        return COST_FOR_FIREBALL;
    }

    public static int[] getmCosts() {
        return mCosts;
    }

    public static void setmCosts(int[] mCosts) {
        Trap.mCosts = mCosts;
    }

    public static int[] getmIcons() {
        return mIcons;
    }

    public static void setmIcons(int[] mIcons) {
        Trap.mIcons = mIcons;
    }

    public String[] getmDescriptions() {
        return mDescriptions;
    }

    public String[] getmNames() {
        return mNames;
    }

    public Tile getmTile() {
        return mTile;
    }

    public void setmTile(Tile mTile) {
        this.mTile = mTile;
    }

    public int getmIconId() {
        return mIconId;
    }

    public void setmIconId(int mIconId) {
        this.mIconId = mIconId;
    }

    public boolean ismIsAttack() {
        return mIsAttack;
    }

    public void setmIsAttack(boolean mIsAttack) {
        this.mIsAttack = mIsAttack;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmPrice() {
        return mPrice;
    }

    public void setmPrice(int mPrice) {
        this.mPrice = mPrice;
    }

    public int getmTrapIndex() {
        return mTrapIndex;
    }

    public void setmTrapIndex(int mTrapIndex) {
        this.mTrapIndex = mTrapIndex;
    }

    public static int MatchIconToIndex(int id) {
            for (int i = 0; i < mIcons.length; i++) {
                if (id == mIcons[i])
                    return i;
            }
        return -1;
    }


    public static int[] GetCosts() {
        return mCosts;
    }

    @Override
    public String toString() {
        if (GetTile() != null)
            return mDescription + " . in [" + GetTile().GetRow() + "][" + GetTile().GetCol() + "]";
        else
            return mDescription;
    }
}
