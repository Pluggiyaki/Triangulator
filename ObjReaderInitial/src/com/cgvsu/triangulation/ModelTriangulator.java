package com.cgvsu.triangulation;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.model.TriangulatedModel;

import java.util.ArrayList;

public class ModelTriangulator {

    // Статический метод для удобного использования
    public static TriangulatedModel triangulate(Model model) {
        return new TriangulatedModel(model);
    }

    // Альтернативный метод с валидацией
    public static TriangulatedModel triangulateWithValidation(Model model) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }

        if (model.polygons == null || model.polygons.isEmpty()) {
            throw new IllegalArgumentException("Model has no polygons");
        }

        return new TriangulatedModel(model);
    }

    // Метод для подсчета треугольников после триангуляции
    public static int countTrianglesAfterTriangulation(Model model) {
        int totalTriangles = 0;

        for (Polygon polygon : model.polygons) {
            int vertexCount = polygon.getVertexIndices().size();
            if (vertexCount < 3) {
                continue; // Пропускаем некорректные полигоны
            }
            totalTriangles += (vertexCount - 2); // n-угольник даёт n-2 треугольника
        }

        return totalTriangles;
    }
}