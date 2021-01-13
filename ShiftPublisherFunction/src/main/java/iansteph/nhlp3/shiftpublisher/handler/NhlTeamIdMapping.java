package iansteph.nhlp3.shiftpublisher.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NhlTeamIdMapping {

    public static final Map<String, Integer> TEAM_NAME_TO_TEAM_ID_MAP = initializeTeamMap();

    private static Map<String, Integer> initializeTeamMap() {

        final Map<String, Integer> map = new HashMap<>();
        map.put("ANAHEIM DUCKS", 24);
        map.put("ARIZONA COYOTES", 53);
        map.put("BOSTON BRUINS", 6);
        map.put("BUFFALO SABRES", 7);
        map.put("CALGARY FLAMES", 20);
        map.put("CAROLINA HURRICANES", 12);
        map.put("CHICAGO BLACKHAWKS", 16);
        map.put("COLORADO AVALANCHE", 21);
        map.put("COLUMBUS BLUE JACKETS", 29);
        map.put("DALLAS STARS", 25);
        map.put("DETROIT RED WINGS", 17);
        map.put("EDMONTON OILERS", 22);
        map.put("FLORIDA PANTHERS", 13);
        map.put("LOS ANGELES KINGS", 26);
        map.put("MINNESOTA WILD", 30);
        map.put("MONTREAL CANADIENS", 8);
        map.put("NASHVILLE PREDATORS", 18);
        map.put("NEW JERSEY DEVILS", 1);
        map.put("NEW YORK ISLANDERS", 2);
        map.put("NEW YORK RANGERS", 3);
        map.put("OTTAWA SENATORS", 9);
        map.put("PHILADELPHIA FLYERS", 4);
        map.put("PITTSBURGH PENGUINS", 5);
        map.put("SAN JOSE SHARKS", 28);
        map.put("ST. LOUIS BLUES", 19);
        map.put("TAMPA BAY LIGHTNING", 14);
        map.put("TORONTO MAPLE LEAFS", 10);
        map.put("VANCOUVER CANUCKS", 23);
        map.put("VEGAS GOLDEN KNIGHTS", 54);
        map.put("WASHINGTON CAPITALS", 15);
        map.put("WINNIPEG JETS", 52);
        return Collections.unmodifiableMap(map);
    }
}
