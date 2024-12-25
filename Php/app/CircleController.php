<?php

class CircleController {
    public function calculate() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['radius'])) {
            $radius = floatval($_POST['radius']);

            if ($radius <= 0) {
                echo json_encode(['error' => 'Радиус должен быть больше нуля.']);
                return;
            }

            $diameter = 2 * $radius;
            $circumference = 2 * M_PI * $radius;
            $area = M_PI * pow($radius, 2);

            echo json_encode([
                'diameter' => $diameter,
                'circumference' => $circumference,
                'area' => $area
            ]);
        } else {
            echo json_encode(['error' => 'Некорректный запрос.']);
        }
    }
}
