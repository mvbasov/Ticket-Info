function debug(){
    if (typeof DEBUG !== 'undefined' && DEBUG) {
        var h = '{ ' +
            '"h_number":"0003123881",' +
            '"h_state":"<font color=\\"Violet\\">DebugJS</font>"' +
        '}';
        jreplace(h);

        var t = '{ ' +
            '"t_valid_days":"1",' +
            '"t_from_datetime":"07.06.2017 16:03",' +
            '"t_to_datetime":"08.06.2017 16:03",' +
            '"t_start_use":"08.06.2017 00:00",' +
            '"t_trip_seq_number":"6",' +
            '"t_trip_start_time":"08.06.2017 at 15:11",' +
            '"t_station":"Krest\'yanskaya zastava",' +
            '"t_station_id":"1531 [0x05bf]",' +
            '"t_transport_type":"Metro",' +
            '"t_layout":"10 (0xa)",' +
            '"t_app_id":"279 (0x117)",' +
            '"t_type_id":"419 (0x1a3)",' +
            '"t_hash":"c662cdc5",' +
            '"t_number":"0003123881",' +
            '"t_ic_uid":"0123456789abcdef",' +
            '"t_file_name":"0003123881-1d-006.txt",' +
            '"t_note_text":"' +
            'DD: 2017-06-05 22:29:53\\n' +
            'м. Беговая (первый)\\n' +
            'Выход к платформе Беговая (7),\\n' +
            'к магазину Чип и Дип (3).",' +
            '"visibility":{' +
            '"vt_note":null,' +
            '"vt_trip":null,' +
            '"vt_station":null' +
            '}' +
        '}';
        jreplace(t);

        var i = '{ ' +
            '"i_manufacturer":"JSC Micron Russia",' +
            '"i_std_bytes":"164",' +
            '"i_names":"' +
            '  MIK1312ED\\n' +
            '  aka К5016ВГ4Н4\\n' +
            '  aka K5016XC1M1H4",' +
            '"i_read_pages":"41",' +
            '"i_read_bytes":"164",' +
            '"i_uid_hi":"01234567",' +
            '"i_uid_lo":"89abcdef",' +
            '"i_bcc0":"33",' +
            '"i_bcc1":"44",' +
            '"i_crc_status":"CRC OK",' +
            '"i_otp":"0111111",' +
            '"i_atqa":"00 44",' +
            '"i_sak":"00",' +
            '"i_get_version":"00 34 21 01 01 00 0e 03",' +
            '"i_counters":"' +
            '  0: 000000\\n' +
            '  1: 000000\\n' +
            '  2: 000000",' +
            '"i_read_sig":"' +
            '  00000000000000000000000000000000\\n' +
            '  00000000000000000000000000000000",' +
            '"i_tech":"nfcA",' +
            '"visibility":{' +
            '"vi_get_version":null,' +
            '"vi_counters":null,' +
            '"vi_read_sig":null' +
            '}' +
        '}';
        jreplace(i);

        var d = '{ ' +
            '"d_content":"' +
            '00:<span class=\\"d_factory\\">f</span>: 34 91 45 68\\n' +
            '01:<span class=\\"d_factory\\">f</span>: a9 a1 b0 96\\n' +
            '02:<span class=\\"d_partial\\">p</span>: 2e 00 70 08\\n' +
            '03:<span class=\\"d_otp\\">o</span>: 00 00 00 00\\n' +
            '04:<span class=\\"d_ro\\">r</span>: 45 da 30 02\\n' +
            '05:<span class=\\"d_ro\\">r</span>: fa aa 9a 00\\n' +
            '06:<span class=\\"d_ro\\">r</span>: 20 c0 12 c8\\n' +
            '07:<span class=\\"d_wr\\">w</span>: 01 25 e0 01\\n' +
            '08:<span class=\\"d_wr\\">w</span>: 06 05 fb 40\\n' +
            '09:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '0a:<span class=\\"d_wr\\">w</span>: c6 62 cd c5\\n' +
            '0b:<span class=\\"d_ro\\">r</span>: 20 c0 12 c8\\n' +
            '0c:<span class=\\"d_wr\\">w</span>: 01 25 e0 01\\n' +
            '0d:<span class=\\"d_wr\\">w</span>: 06 05 fb 40\\n' +
            '0e:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '0f:<span class=\\"d_wr\\">w</span>: c6 62 cd c5\\n' +
            '10:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '11:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '12:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '13:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '14:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '15:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '16:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '17:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '18:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '19:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '1a:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '1b:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '1c:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '1d:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '1e:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '1f:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '20:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '21:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '22:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '23:<span class=\\"d_wr\\">w</span>: 00 00 00 00\\n' +
            '24:<span class=\\"d_special\\">s</span>: 00 00 00 bd\\n' +
            '25:<span class=\\"d_special\\">s</span>: 00 00 00 ff\\n' +
            '26:<span class=\\"d_special\\">s</span>: 00 05 00 00\\n' +
            '27:<span class=\\"d_special\\">s</span>: 00 00 00 00\\n' +
            '28:<span class=\\"d_special\\">s</span>: 00 00 00 00\\n' +
            '---\\n' +
            '[!]Last block\\n' +
            'valid pages: 1"' +
        '}';
        jreplace(d);

    }
}


