package me.msicraft.towerRpg.Party.Data;

import me.msicraft.towerRpg.API.Data.Pair;

import java.util.*;

public class TempPartyInfo {

    private final Map<Party.PartyOptions, Object> partyOptionsMap = new HashMap<>();

    public TempPartyInfo() {
        for (Party.PartyOptions option : Party.PartyOptions.values()) {
            partyOptionsMap.put(option, option.getBaseValue());
        }
    }

    public void setPartyOption(Party.PartyOptions option, Object value) {
        partyOptionsMap.put(option, value);
    }

    public Object getValue(Party.PartyOptions option) {
        return partyOptionsMap.get(option);
    }

    public Set<Pair<Party.PartyOptions, Object>> getPartyOptionValueList() {
        Set<Pair<Party.PartyOptions, Object>> pairSet = new HashSet<>();
        for (Party.PartyOptions option : partyOptionsMap.keySet()) {
            Pair<Party.PartyOptions, Object> pair = new Pair<>(option, partyOptionsMap.get(option));
            pairSet.add(pair);
        }
        return pairSet;
    }

}
