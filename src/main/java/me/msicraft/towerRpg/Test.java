package me.msicraft.towerRpg;

public class Test {

    public static void main(String[] args) {
        String m = "When picked up, ambers refund &9{percent}% &7of your missing mana.";
        if (m.contains("{percent}")) {
            m = m.replaceAll("\\{percent}", "x");
        }
        System.out.println(m);
    }

}
