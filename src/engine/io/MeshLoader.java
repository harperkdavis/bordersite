package engine.io;

import engine.graphics.Material;
import engine.graphics.mesh.Mesh;
import engine.graphics.mesh.MeshBuilder;
import engine.graphics.vertex.Vertex;
import engine.math.Vector2;
import engine.math.Vector3;
import org.lwjgl.assimp.*;

public class MeshLoader {

    public static Mesh loadModel(String filePath, Material material) {
        AIScene scene = Assimp.aiImportFile("resources/models/" + filePath, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate);

        if (scene == null) {
            System.err.println("Couldn't load model at " + filePath);
            return MeshBuilder.Cube(1.0f, Material.DEFAULT);
        }

        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));
        int vertexCount = mesh.mNumVertices();

        AIVector3D.Buffer vertices = mesh.mVertices();
        AIVector3D.Buffer normals = mesh.mNormals();

        Vertex[] vertexList = new Vertex[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            AIVector3D vertex = vertices.get(i);
            Vector3 meshVertex = new Vector3(vertex.x(), vertex.y(), vertex.z());

            AIVector3D normal = normals.get(i);
            Vector3 meshNormal = new Vector3(normal.x(), normal.y(), normal.z()); // TODO: turn normals into lighting

            Vector2 meshTextureCoord = new Vector2(0, 0);
            if (mesh.mNumUVComponents().get(0) != 0) {
                AIVector3D texture = mesh.mTextureCoords(0).get(i);
                meshTextureCoord.setX(texture.x());
                meshTextureCoord.setY(texture.y());
            }

            vertexList[i] = new Vertex(meshVertex, meshNormal, meshTextureCoord);
        }

        int faceCount = mesh.mNumFaces();
        AIFace.Buffer indices = mesh.mFaces();
        int[] indicesList = new int[faceCount * 3];

        for (int i = 0; i < faceCount; i++) {
            AIFace face = indices.get(i);
            indicesList[i * 3] = face.mIndices().get(0);
            indicesList[i * 3 + 1] = face.mIndices().get(1);
            indicesList[i * 3 + 2] = face.mIndices().get(2);
        }

        return new Mesh(vertexList, indicesList, material);
    }

}
