package com.cgvsu.objwriter;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class ObjWriter {

    public static void write(Model model, String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            write(model, writer);
        }
    }

    public static void write(Model model, FileWriter writer) throws IOException {
        writeVertices(model.vertices, writer);

        writeTextureVertices(model.textureVertices, writer);

        writeNormals(model.normals, writer);

        writePolygons(model.polygons, writer);

        writer.flush();
    }

    private static void writeVertices(ArrayList<Vector3f> vertices, FileWriter writer) throws IOException {
        for (Vector3f vertex : vertices) {
            // Используем Locale.US чтобы десятичный разделитель был точкой (.)
            String line = String.format(Locale.US, "v %.6f %.6f %.6f\n",
                    vertex.x, vertex.y, vertex.z);
            writer.write(line);
        }
        if (!vertices.isEmpty()) {
            writer.write("\n");
        }
    }

    private static void writeTextureVertices(ArrayList<Vector2f> textureVertices, FileWriter writer) throws IOException {
        if (textureVertices.isEmpty()) {
            return;
        }

        for (Vector2f textureVertex : textureVertices) {
            String line = String.format(Locale.US, "vt %.6f %.6f\n",
                    textureVertex.x, textureVertex.y);
            writer.write(line);
        }
        writer.write("\n");
    }

    private static void writeNormals(ArrayList<Vector3f> normals, FileWriter writer) throws IOException {
        if (normals.isEmpty()) {
            return;
        }

        for (Vector3f normal : normals) {
            String line = String.format(Locale.US, "vn %.6f %.6f %.6f\n",
                    normal.x, normal.y, normal.z);
            writer.write(line);
        }
        writer.write("\n");
    }

    private static void writePolygons(ArrayList<Polygon> polygons, FileWriter writer) throws IOException {
        for (Polygon polygon : polygons) {
            StringBuilder line = new StringBuilder("f ");

            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            ArrayList<Integer> textureIndices = polygon.getTextureVertexIndices();
            ArrayList<Integer> normalIndices = polygon.getNormalIndices();

            boolean hasTextures = !textureIndices.isEmpty() && textureIndices.size() == vertexIndices.size();
            boolean hasNormals = !normalIndices.isEmpty() && normalIndices.size() == vertexIndices.size();

            // формируем строку для каждого индекса полигона
            for (int i = 0; i < vertexIndices.size(); i++) {
                int vertexIndex = vertexIndices.get(i) + 1;

                if (hasTextures && hasNormals) {
                    // Формат: f v/vt/vn v/vt/vn v/vt/vn
                    int textureIndex = textureIndices.get(i) + 1;
                    int normalIndex = normalIndices.get(i) + 1;
                    line.append(String.format("%d/%d/%d", vertexIndex, textureIndex, normalIndex));
                } else if (hasTextures) {
                    // Формат: f v/vt v/vt v/vt
                    int textureIndex = textureIndices.get(i) + 1;
                    line.append(String.format("%d/%d", vertexIndex, textureIndex));
                } else if (hasNormals) {
                    // Формат: f v//vn v//vn v//vn
                    int normalIndex = normalIndices.get(i) + 1;
                    line.append(String.format("%d//%d", vertexIndex, normalIndex));
                } else {
                    // Формат: f v v v
                    line.append(vertexIndex);
                }

                if (i < vertexIndices.size() - 1) {
                    line.append(" ");
                }
            }

            line.append("\n");
            writer.write(line.toString());
        }
    }

    /**
     * Проверяет, что все индексы в полигонах валидны
     * @param model Модель для проверки
     * @return true если все индексы валидны
     */
    public static boolean validateModelIndices(Model model) {
        int vertexCount = model.vertices.size();
        int textureCount = model.textureVertices.size();
        int normalCount = model.normals.size();

        for (Polygon polygon : model.polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            ArrayList<Integer> textureIndices = polygon.getTextureVertexIndices();
            ArrayList<Integer> normalIndices = polygon.getNormalIndices();

            // проверяем вершины
            for (int index : vertexIndices) {
                if (index < 0 || index >= vertexCount) {
                    System.err.printf("Invalid vertex index: %d (max: %d)%n", index, vertexCount - 1);
                    return false;
                }
            }

            // проверяем текстурные координаты (если есть)
            if (!textureIndices.isEmpty()) {
                if (textureIndices.size() != vertexIndices.size()) {
                    System.err.println("Texture indices count doesn't match vertex indices count");
                    return false;
                }

                for (int index : textureIndices) {
                    if (index < 0 || index >= textureCount) {
                        System.err.printf("Invalid texture index: %d (max: %d)%n", index, textureCount - 1);
                        return false;
                    }
                }
            }

            // проверяем нормали (если есть)
            if (!normalIndices.isEmpty()) {
                if (normalIndices.size() != vertexIndices.size()) {
                    System.err.println("Normal indices count doesn't match vertex indices count");
                    return false;
                }

                for (int index : normalIndices) {
                    if (index < 0 || index >= normalCount) {
                        System.err.printf("Invalid normal index: %d (max: %d)%n", index, normalCount - 1);
                        return false;
                    }
                }
            }
        }

        return true;
    }
}