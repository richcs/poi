/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.ddf;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.apache.poi.POIDataSamples;
import org.apache.poi.hssf.HSSFTestDataSamples;
import org.apache.poi.poifs.storage.RawDataUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.NullOutputStream;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestEscherDump {
    static NullPrinterStream nullPS;
    
    @BeforeClass
    public static void init() throws UnsupportedEncodingException {
        nullPS = new NullPrinterStream();
    }

    // simple test to at least cover some parts of the class
    @Test
    public void testSimple() throws Exception {
        final String recordData =
            "H4sIAAAAAAAAAL2UaVCSWxjHX0SBChABLRXM1FxSEzXTzHK7dpVIcMmwxXCP9KaGTaWlGYLrtGmGmYEmYmqF2qIt4ppmjNG+" +
            "2dWulUtOUdq1NHjva8v90HT7eM+Z5znP/M9/zpk5v3mONgAoc5AANBDKeVDW0gQAjZkVCti3mKnpAExpB/m8AKTyEiTCNd2J" +
            "Z+O0o6W+srDCyH3DhzkgUAD76v86QNA4mKTMg4QfnUew/5qA29CZz6ALqGcSgNzOICB05gD1rhODJR2AZu3Ox3YOKAfVUPhH" +
            "ULtbpdVJ0ccdijw0pY1A56M3Jup7U15jp7X4PPTTecx92/MT9eZwtUICrLJvsB6z0fHG5qbw7mRpFRaOnPYJ6SqXd5AQMSKM" +
            "jceyMD4NsULkj1OwHncz5cO3pPvCXXPTMNdNa+kDfwku4q0RnFL8YGBI6N+oXHlgzCkGWGRdONJPK1PbusJrhBltylPBMm3e" +
            "G0kw6DGdLhoU3pmgJ6n1maC1fXrs0uUL6cWG/kGVm3MWHh3pALq4+PH55k7Uu3d+x85u9zxwIzfQuU+3TIG5SkOgrS1tCJb3" +
            "3nqHrxcx3XlJA6vZJ6Oi1ctaXppQyBQLbLLrPJaKKq+zIexFLrVdZM+r34pJJpNN1hSrWbM/lIyRmKpYRIi7CybTTUzBWt49" +
            "11HRM/VbCiZ6Gyt9TZmhGXPS75xYjpH366vhgLJu4ZoZfM+/4FvGaBZIE9aZ2SduMrUT4mJA4NpP8F2AhB+dT+D/jY/7DZ84" +
            "ULbaK4C4crJvZ4qej2+em2+Vni4mPluh2e5xyoGUWYRaoFnWubHcaX+L09Ya0ta4KrP13C2ozMyicr4ovY0fNhT2wfMF7ip8" +
            "/tD0u96myXcn92gtTnEuGfBJxY0lFG0mJxPWpknjNxmzWvzKj18rpjO4hhQXAtaRVSmJu+D8egI3RdQVXYxzRhs1+HE2iNvM" +
            "fVe2DsSjqJQbBdUajcaECC3/58MP97Q0Eo+GNTdKbhk1r7UJadrVj0rLplmAqU/BlGeXDObGLtl69vITp9tD25vVY9vUT17u" +
            "WTGW8idcxUDMMX2PHa8X6xzG0C5cGJcVth40m3ycwCpcfuP4OClu6IpysV/9QuvrdW/Yb3Qj6Ul7e3nybqemdkvLXsXG2N3v" +
            "qeVE0woXb06pLduuFWUv7NxY8jq1k63fcD5jvG/w/IE8eUNh0Pohz0WRx6tdOlf4XhlbF5pgfYYzn8w6cjYx/8rBXvtWNz8L" +
            "6uu+ig35t+dgOc4jOpLirmFPtjQdKHovGZ4Bff4LaIPLnx6cbnKFo8JHDoGpJ1+BwKGfgM6GhB+d+F/0acj3PiVEABoProzN" +
            "1dcsVo9TPoPIF+r9EQI0qtveV4WEI1YhFjfmLxDsyFJptHvx/0BD3bfKJY/XqlFTReyIko4tQSzFFRyGRbkyg7MyuCqTmsiA" +
            "mAgs3FGB0BOR7LzNuUYMC9QWaXyUTcxELLOFQvaRIQZ1HlgkJtE25Ohym/xzkqxqbFI1fWKsGgKq0m/q9kwkVDJAvdKM+7c8" +
            "wj8GVPdneU0GVaeLVO6Kd3T2lMQFRNeCRwUyx5LSIxI5RmIFNc2RnuSIfYOeOZ+0CwzE7BFTJO+5cVeUz2nDN7mMYUSYOZyv" +
            "SyyaRRydLKPYMmqFbS5K8RJ6vQNIGtiuI8AKCEgXsqV9Vz1tgvzovKiD2FPtpNgRlb0keoprdS+hnsP6ICwLBrE9dz26g2YP" +
            "DszibWNE7zW5xndwlsoqFRh87XTFw8BXiFdv0SDsGBnfNcOu/Qu7y7SLppfzLJq714byzYQ590BA+BN2xyDhR+fZX7CL/s5u" +
            "Q9f/8ccWX28U3BaGw9qTiSqDfOtHmXXZq8XiHXAYoz901c5V2lVulTXZEMqwnLq8+8ds95s0FFrdl73saRntr/UuUxFHY0WU" +
            "z5b333qXTe/NagSRrmqkqypoNG12Oz3nE5Yzyt7d05eY66Ci2oTR+rNS3K4OiClGK+07HWtFFLvAqv6sNkpFsLs4Wp8XfRp/" +
            "11oPk3uTQB0ftHg1C16KRTBl+AbCzVaYfx6VFlJ7GL7Jme8bVOku8FKZL0eGgMVk4qhEnpZogNrtFU5yEyswJ+LbHOKsOPCn" +
            "cT19LR+PfTgjN4CKCS5Es4LS+7nLt9hQ7ejwGQnEyxebOgJzlHjotWUACpoZsFkAgGqBeUDZAzB6h4N2MFCNhmIuFJMAgPsH" +
            "eJr+iZEHAAA=";

        // Create a new instance of the escher dumper
        EscherDump dumper = new EscherDump();

        // Decode the stream to bytes
        byte[] bytes = RawDataUtil.decompress(recordData);
        // Dump the contents of scher to screen.
        dumper.dump(bytes, 0, bytes.length, nullPS);

        dumper.dump(0, new byte[] {}, nullPS);
        dumper.dump(new byte[] {}, 0, 0, nullPS);
    }

    @Test
    public void testWithData() throws Exception {
        new EscherDump().dump(8, new byte[] {0, 0, 0, 0, 0, 0, 0, 0}, nullPS);
    }

    @Test
    public  void testWithSamplefile() throws Exception {
        //InputStream stream = HSSFTestDataSamples.openSampleFileStream(")
        byte[] data = POIDataSamples.getDDFInstance().readFile("Container.dat");
        new EscherDump().dump(data.length, data, nullPS);
        //new EscherDump().dumpOld(data.length, new ByteArrayInputStream(data), System.out);
        
        data = new byte[2586114];
        try (InputStream stream = HSSFTestDataSamples.openSampleFileStream("44593.xls")) {
            int bytes = IOUtils.readFully(stream, data);
            assertTrue(bytes != -1);
            //new EscherDump().dump(bytes, data, System.out);
            //new EscherDump().dumpOld(bytes, new ByteArrayInputStream(data), System.out);
        }
    }
    
    /**
     * Implementation of an OutputStream which does nothing, used
     * to redirect stdout to avoid spamming the console with output
     */
    private static class NullPrinterStream extends PrintStream {
        @SuppressWarnings("resource")
        private NullPrinterStream() throws UnsupportedEncodingException {
            super(new NullOutputStream(),true,LocaleUtil.CHARSET_1252.name());
        }      
    }    
}
