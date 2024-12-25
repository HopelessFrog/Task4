<?php

require_once 'CircleController.php';

header('Content-Type: application/json');
$controller = new CircleController();
$controller->calculate();
