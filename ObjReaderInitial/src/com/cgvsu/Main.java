package com.cgvsu;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.model.TriangulatedModel;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.triangulation.ModelTriangulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            // Загружаем модель
            Path fileName = Path.of("D:/ТаскиКГ/CGVSU-main/3DModels/Faceform/WrapHead.obj");
            String fileContent = Files.readString(fileName);

            System.out.println("Loading model ...");
            Model originalModel = ObjReader.read(fileContent);

            // Выводим информацию об исходной модели
            printModelInfo("Original Model", originalModel);

            // Триангулируем модель
            System.out.println("\n=== Starting Triangulation ===");
            TriangulatedModel triangulatedModel = ModelTriangulator.triangulate(originalModel);

            // Выводим информацию о триангулированной модели
            printModelInfo("Triangulated Model", triangulatedModel);

            // Дополнительная информация
            System.out.println("\n=== Additional Information ===");
            System.out.println("All polygons are triangles: " + triangulatedModel.allPolygonsAreTriangles());
            System.out.println("Expected triangles: " + ModelTriangulator.countTrianglesAfterTriangulation(originalModel));
            System.out.println("Actual triangles: " + triangulatedModel.polygons.size());

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printModelInfo(String title, Model model) {
        System.out.println("\n" + title + ":");
        System.out.println("  Vertices: " + model.vertices.size());
        System.out.println("  Texture vertices: " + model.textureVertices.size());
        System.out.println("  Normals: " + model.normals.size());
        System.out.println("  Polygons: " + model.polygons.size());

        // Подсчет полигонов по количеству вершин
        int triangles = 0, quads = 0, other = 0;
        for (Polygon polygon : model.polygons) {
            int vertexCount = polygon.getVertexIndices().size();
            if (vertexCount == 3) {
                triangles++;
            } else if (vertexCount == 4) {
                quads++;
            } else {
                other++;
            }
        }

        System.out.println("  Polygon types:");
        System.out.println("    Triangles: " + triangles);
        System.out.println("    Quads: " + quads);
        System.out.println("    Other (" + (triangles+quads+other > 0 ? "n>4" : "none") + "): " + other);
    }
}