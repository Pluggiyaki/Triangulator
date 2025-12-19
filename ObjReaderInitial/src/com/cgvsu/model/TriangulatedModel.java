package com.cgvsu.model;

import java.util.ArrayList;

public class TriangulatedModel extends Model {

    public TriangulatedModel(Model originalModel) {
        // копируем все данные из исходной модели
        this.vertices = new ArrayList<>(originalModel.vertices);
        this.textureVertices = new ArrayList<>(originalModel.textureVertices);
        this.normals = new ArrayList<>(originalModel.normals);

        // триангулируем полигоны
        this.polygons = triangulatePolygons(originalModel.polygons);
    }

    private ArrayList<Polygon> triangulatePolygons(ArrayList<Polygon> originalPolygons) {
        ArrayList<Polygon> triangulated = new ArrayList<>();

        for (Polygon polygon : originalPolygons) {
            triangulated.addAll(triangulateSinglePolygon(polygon));
        }

        return triangulated;
    }

    private ArrayList<Polygon> triangulateSinglePolygon(Polygon polygon) {
        ArrayList<Polygon> triangles = new ArrayList<>();
        ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
        ArrayList<Integer> textureIndices = polygon.getTextureVertexIndices();
        ArrayList<Integer> normalIndices = polygon.getNormalIndices();

        int vertexCount = vertexIndices.size();

        // проверка на минимальное количество вершин
        if (vertexCount < 3) {
            throw new IllegalArgumentException("Polygon must have at least 3 vertices");
        }

        // если полигон уже треугольник — возвращаем его как есть
        if (vertexCount == 3) {
            triangles.add(polygon);
            return triangles;
        }

        // соединяем вершину 0 с вершинами i и i+1
        for (int i = 1; i < vertexCount - 1; i++) {
            Polygon triangle = new Polygon();

            // создаем списки индексов для треугольника
            ArrayList<Integer> triVertexIndices = new ArrayList<>();
            triVertexIndices.add(vertexIndices.get(0));
            triVertexIndices.add(vertexIndices.get(i));
            triVertexIndices.add(vertexIndices.get(i + 1));

            triangle.setVertexIndices(triVertexIndices);

            // обрабатываем текстурные координаты, если они есть
            if (!textureIndices.isEmpty() && textureIndices.size() == vertexCount) {
                ArrayList<Integer> triTextureIndices = new ArrayList<>();
                triTextureIndices.add(textureIndices.get(0));
                triTextureIndices.add(textureIndices.get(i));
                triTextureIndices.add(textureIndices.get(i + 1));
                triangle.setTextureVertexIndices(triTextureIndices);
            }

            // обрабатываем нормали, если они есть
            if (!normalIndices.isEmpty() && normalIndices.size() == vertexCount) {
                ArrayList<Integer> triNormalIndices = new ArrayList<>();
                triNormalIndices.add(normalIndices.get(0));
                triNormalIndices.add(normalIndices.get(i));
                triNormalIndices.add(normalIndices.get(i + 1));
                triangle.setNormalIndices(triNormalIndices);
            }

            triangles.add(triangle);
        }

        return triangles;
    }

    // дополнительный метод для проверки, что все полигоны — треугольники
    public boolean allPolygonsAreTriangles() {
        for (Polygon polygon : polygons) {
            if (polygon.getVertexIndices().size() != 3) {
                return false;
            }
        }
        return true;
    }
}