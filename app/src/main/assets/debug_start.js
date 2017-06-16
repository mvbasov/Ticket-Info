function debug(){
    if (typeof DEBUG !== 'undefined' && DEBUG) {
        var  msg_json = '{' +
            '"w_header":"Welcome!",' +
            '"error_msg":"reading",' +
            '"visibility":[' +
            '"error_msg"' +
            ']' +
        '}';
        jreplace(msg_json);
    }
}


