package Logic;

import com.example.cdv.amazingmaze.R;

/**
 * Created by אילון on 30/01/2017.
 */

public class Trap {
    public static final int FIRE = 1;
    public static final int FIVE_STEPS = 1;
    public static final int TEN_STEPS = 2;
    public static final int TWENTY_STEPS = 3;
    public static final int FORTY_STEPS = 4;

    public static final int COST_FOR_5_STEP_SHIELD = 5;
    public static final int COST_FOR_10_STEP_SHIELD = 10;
    public static final int COST_FOR_20_STEP_SHIELD = 20;
    public static final int COST_FOR_40_STEP_SHIELD = 40;
    public final int COST_FOR_FIREBALL = 30;

    public static int[] defenceCosts =
            {0,COST_FOR_5_STEP_SHIELD,COST_FOR_10_STEP_SHIELD, COST_FOR_20_STEP_SHIELD, COST_FOR_40_STEP_SHIELD};

    private int[] attackCosts =
            {0,COST_FOR_FIREBALL/*,COST_FOR_SWORD*/};

    private static int[] defenceIcons =
            {0,R.mipmap.shield_5_steps, R.mipmap.shield_10_steps, R.mipmap.shield_20_steps, R.mipmap.shield_40_steps};
    private static int[] attackIcons =
            {0,R.mipmap.fireball/*, R.mipmap.sword*/};
    private final String[] descriptions_defence =
            {"","Protects you from attacks for 5 steps ("+COST_FOR_5_STEP_SHIELD+" COINS)", "Protects you from attacks for 10 steps ("+COST_FOR_10_STEP_SHIELD+" COINS)", "Protects you from attacks for 20 steps ("+COST_FOR_20_STEP_SHIELD+" COINS)", "Protects you from attacks for 40 steps ("+COST_FOR_40_STEP_SHIELD+" COINS)"};
    private final String[] names_attack =
            {"","Fire", "Sword"};
    private final String[] names_defence =
            {"","Shield (5 steps)", "Shield (10 steps)", "Shield (20 steps)", "Shield (40 steps)"};
    private final String[] descriptions_attack =
            {"","Set the path on fire ("+COST_FOR_FIREBALL+" COINS)"/*, "Cut your rival! ("+COST_FOR_SWORD+" COINS)"*/};


    private Tile mTile;
    private int mIconId;
    private boolean mIsAttack;
    private String mDescription;
    private String mName;
    private int mPrice;
    private int mTrapIndex;


    public Trap(int trapsIndex) {
        if (trapsIndex < 0) {
            mIsAttack = false;
            trapsIndex = trapsIndex * -1;
        }
        else
            mIsAttack = true;
        this.mTrapIndex = trapsIndex;
        if (mIsAttack && trapsIndex < attackIcons.length) {
            this.mIconId = attackIcons[trapsIndex];
            this.mDescription = descriptions_attack[trapsIndex];
            this.mName = names_attack[trapsIndex];
            this.mPrice = attackCosts[trapsIndex];
        }
        if (!mIsAttack && trapsIndex < defenceIcons.length) {
            this.mIconId = defenceIcons[trapsIndex];
            this.mDescription = descriptions_defence[trapsIndex];
            this.mName = names_defence[trapsIndex];
            this.mPrice = defenceCosts[trapsIndex];
        }
    }

    public int GetPrice() {
        return mPrice;
    }

    public void SetPrice(int price) {
        this.mPrice = price;
    }

    public int GetIconResource() {
        if (mIsAttack)
            return attackIcons[mIconId];
        else
            return defenceIcons[mIconId];
    }

    public int GetTrapIndex() {
            return mTrapIndex;
    }


    public void SetTrapIndex(int trapIndex) {
        this.mTrapIndex = trapIndex;
    }


    public int[] GetDefenceIcons() {
        return defenceIcons;
    }

    public void SetDefenceIcons(int[] defenceIcons) {
        this.defenceIcons = defenceIcons;
    }

    public int[] GetAttackIcons() {
        return attackIcons;
    }

    public void SetAttackIcons(int[] attackIcons) {
        this.attackIcons = attackIcons;
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

    public String[] SetDescriptions_defence() {
        return descriptions_defence;
    }

    public String[] GetNames_attack() {
        return names_attack;
    }

    public String[] GetNames_defence() {
        return names_defence;
    }

    public String[] GetDescriptions_attack() {
        return descriptions_attack;
    }

    public static int MatchIconToIndex(int id, boolean isOnAttack) {
        if (isOnAttack) {
            for (int i = 0; i < attackIcons.length; i++) {
                if (id == attackIcons[i])
                    return i;
            }
        } else {
            for (int i = 0; i < defenceIcons.length; i++) {
                if (id == defenceIcons[i])
                    return i;
            }
        }
        return -1;
    }

    public static int MatchIndexToIcon(int iconId, boolean isOnAttack) {
        if (isOnAttack) {
            if (iconId < attackIcons.length)
                return attackIcons[iconId];
        }
         else
            if (iconId < defenceIcons.length)
                return defenceIcons[iconId];

        return -1;
    }

    public static int[] GetDefenceCosts() {
        return defenceCosts;
    }

    @Override
    public String toString() {
        if (GetTile() != null)
            return mDescription + " . in ["+ GetTile().GetRow()+"]["+ GetTile().GetCol()+"]";
        else
            return mDescription;
    }
}
