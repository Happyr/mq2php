<?php

set_time_limit(0);

/*
 * Look both in $_SERVER, $_POST and $argv after some data.
 */
$data = null;
if (isset($_SERVER['DEFERRED_DATA'])) {
    $data = $_SERVER['DEFERRED_DATA'];
} elseif (isset($_POST['DEFERRED_DATA'])) {
    $data = $_POST['DEFERRED_DATA'];
} elseif (isset($argv)) {
    // if shell
    if (isset($argv[1])) {
        $data = urldecode($argv[1]);
    }
}

if ($data === null) {
    trigger_error('No message data found', E_USER_WARNING);
    exit(1);
}


$message = base64_decode($data);
$headers = array();
$body = null;
$lines = explode("\n", $message);
foreach ($lines as $i=>$line) {
    if ($line == '') {
        $body = $lines[$i+1];
        break;
    }
    list($name, $value) = explode(':', $line, 2);
    $headers[$name]= trim($value);
}

//$headers is an array with headers
//$body is the content of the message
