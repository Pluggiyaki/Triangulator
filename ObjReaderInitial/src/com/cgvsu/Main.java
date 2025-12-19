package com.cgvsu;

import com.cgvsu.model.Model;
import com.cgvsu.model.TriangulatedModel;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.triangulation.ModelTriangulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        Path fileName = Path.of("keytruck.obj");

        if (!Files.exists(fileName)) {
            fileName = Path.of("3DModels/Faceform/WrapHead.obj");
            if (!Files.exists(fileName)) {
                fileName = Path.of("../3DModels/Faceform/WrapHead.obj");
                if (!Files.exists(fileName)) {
                    System.err.println("Model file not found!");
                    System.err.println("Tried:");
                    System.err.println("  - original_model.obj");
                    System.err.println("  - 3DModels/Faceform/WrapHead.obj");
                    System.err.println("  - ../3DModels/Faceform/WrapHead.obj");
                    return;
                }
            }
        }

        System.out.println("Loading model from: " + fileName.toAbsolutePath());
        String fileContent = Files.readString(fileName);

        System.out.println("Loading model ...");
        Model originalModel = ObjReader.read(fileContent);

        System.out.println("\n=== Original Model ===");
        printModelInfo(originalModel);

        System.out.println("\nSaving original model to 'original_model.obj'...");
        ObjWriter.write(originalModel, "original_model.obj");

        System.out.println("\n=== Triangulating Model ===");
        TriangulatedModel triangulatedModel = new TriangulatedModel(originalModel);
        printModelInfo(triangulatedModel);

        boolean allTriangles = triangulatedModel.allPolygonsAreTriangles();
        System.out.println("All polygons are triangles: " + allTriangles);

        if (!allTriangles) {
            System.out.println("Warning: Not all polygons are triangles!");
        }

        // проверяем индексы перед сохранением
        System.out.println("\nValidating model indices...");
        boolean isValid = ObjWriter.validateModelIndices(triangulatedModel);
        System.out.println("Model indices are valid: " + isValid);

        if (!isValid) {
            System.out.println("Warning: Model has invalid indices!");
        }

        // сохраняем триангулированную модель
        System.out.println("\nSaving triangulated model to 'triangulated_model.obj'...");
        ObjWriter.write(triangulatedModel, "triangulated_model.obj");

        System.out.println("\nDone! Files saved:");
        System.out.println("  - original_model.obj");
        System.out.println("  - triangulated_model.obj");

        // сравниваем размеры файлов
        try {
            long originalSize = Files.size(Path.of("original_model.obj"));
            long triangulatedSize = Files.size(Path.of("triangulated_model.obj"));

            System.out.println("\nFile sizes:");
            System.out.printf("  Original: %,d bytes%n", originalSize);
            System.out.printf("  Triangulated: %,d bytes%n", triangulatedSize);
            System.out.printf("  Difference: %,d bytes%n", triangulatedSize - originalSize);
        } catch (IOException e) {
            System.err.println("Could not compare file sizes: " + e.getMessage());
        }
    }

    private static void printModelInfo(Model model) {
        System.out.println("Vertices: " + model.vertices.size());
        System.out.println("Texture vertices: " + model.textureVertices.size());
        System.out.println("Normals: " + model.normals.size());
        System.out.println("Polygons: " + model.polygons.size());

        // подсчет треугольников и других полигонов
        int triangleCount = 0;
        int quadCount = 0;
        int ngonCount = 0;

        for (com.cgvsu.model.Polygon poly : model.polygons) {
            int vertexCount = poly.getVertexIndices().size();
            if (vertexCount == 3) {
                triangleCount++;
            } else if (vertexCount == 4) {
                quadCount++;
            } else {
                ngonCount++;
            }
        }

        System.out.println("Triangles: " + triangleCount);
        System.out.println("Quads: " + quadCount);
        System.out.println("N-gons (>4): " + ngonCount);
    }
}