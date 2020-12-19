<?php
namespace MyApp;
use Ratchet\MessageComponentInterface;
use Ratchet\ConnectionInterface;

class chat implements MessageComponentInterface {

    private $clients;
     public function __construct()
     {

        $this->clients = array();
     }
    public function onOpen(ConnectionInterface $conn) {
        $this->clients[]=$conn;
        echo"New connection";

    }

    public function onMessage(ConnectionInterface $from, $msg) {
        foreach($this -> clients as $client){
            if($client != $from){
                $client-> send($msg);
            }
        }
    }

    public function onClose(ConnectionInterface $conn) {
        
        echo"connection closed . Connection.id:".$conn->getClientId();
    }

    public function onError(ConnectionInterface $conn, \Exception $e) {
    }
}
?>