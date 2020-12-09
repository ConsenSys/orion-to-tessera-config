package net.consensys.tessera.migration;

import org.junit.Test;

public class MainTest {

    @Test
    public void main() throws Exception{

        String file = MainTest.class.getResource("/fullConfigTest.toml").getFile();
        Main.main("orionfile="+ file,"skipValidation=true","outputfile=".concat("build/genereated.json"),"-v");

    }

}
