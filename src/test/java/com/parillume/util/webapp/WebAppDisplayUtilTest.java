/*
 * Copyright(c) 2024, Parillume Inc., All rights reserved worldwide
 */
package com.parillume.util.webapp;

import com.parillume.model.external.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class WebAppDisplayUtilTest {
    @Test
    public void testMapDisplayNames() {
        List<User> users = new ArrayList<>();
        
        users.add( new User("Steve", "Bbuio") );
        users.add( new User("Rick", "Bccgb") );
        users.add( new User("Rick", "Aaa") );
        users.add( new User("Steve", "Cbyui") );
        users.add( new User("Steve", "Cbxer") );
        users.add( new User("Rick", "Bbxnk") );
        users.add( new User("Rick", "B") );
        users.add( new User("Sandra", "B") );
        users.add( new User("Steve", "A") );
        users.add( new User("Rick", "Cbyui") );
        users.add( new User("Steve", "Banmjk") );
        users.add( new User("Rick", "Bayqwe") );
        users.add( new User("Steve", "Bbxnmk") );
        users.add( new User("Rick", "Ba") );
        users.add( new User("Rick", "Baxe") );
        users.add( new User("Steve", "Baxydfg") );
        users.add( new User("Rick", "Cbxert") );
        users.add( new User("Steve", "Bccgb") );
        users.add( new User("Steve", "B") );
        users.add( new User("Steve", "Bayq") );
        users.add( new User("Rick", "Baxydfg") );
        users.add( new User("Steve", "Canmk") );
        users.add( new User("Rick", "Bbuio") );
        users.add( new User("Rick", "Canmk") );
        users.add( new User("Steve", "Baxert") );
        
        Map<User,String> map = WebAppDisplayUtil.getDisplayNames(users);
        List<String> expectedNames = new ArrayList( Arrays.asList(
                                                        "Rick Aaa",
                                                        "Rick B",
                                                        "Rick Ba",
                                                        "Rick Baxe",
                                                        "Rick Baxy",
                                                        "Rick Bayq",
                                                        "Rick Bbui",
                                                        "Rick Bbxn",
                                                        "Rick Bccg",
                                                        "Rick Canm",
                                                        "Rick Cbxe",
                                                        "Rick Cbyu",
                                                        "Sandra",
                                                        "Steve A",
                                                        "Steve B",
                                                        "Steve Banm",
                                                        "Steve Baxe",
                                                        "Steve Baxy",
                                                        "Steve Bayq",
                                                        "Steve Bbui",
                                                        "Steve Bbxn",
                                                        "Steve Bccg",
                                                        "Steve Canm",
                                                        "Steve Cbxe",
                                                        "Steve Cbyu" ));
        int i=0;
        for(User u: map.keySet()) {
            assertEquals(expectedNames.get(i++), map.get(u));
        }
    }
}
