function debug(){
    if (typeof DEBUG !== 'undefined' && DEBUG) {
        var  msg_json = '{' +
            '"w_header":"Welcome!",' +
            '"w_msg":"reading",' +
            '"visibility":{' +
            '"vw_msg":null' +
            '}' +
        '}';
        jreplace(msg_json);
    }
}


