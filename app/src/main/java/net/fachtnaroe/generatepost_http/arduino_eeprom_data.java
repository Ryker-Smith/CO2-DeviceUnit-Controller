package net.fachtnaroe.generatepost_http;

class arduino_eeprom_data {
        private static final int max_SSID = 32;
        private static final int max_PSK = 64;
        private static final int max_DeviceName = 32;

        public char active;
        public byte config_Status;
        public byte config_Attempts;
        public char[] config_SSID = new char[max_SSID];
        public char[] config_PSK = new char[max_PSK];
        public char[] config_DeviceName = new char[max_DeviceName];
        public String rebootstring=new utils().rebootstring();
}

class utils {
        String rebootstring() {
          String s="/";
          s+="athbhútáil";
          // th intention is to add the partial MAC address of the device here
          return s;
        }
}