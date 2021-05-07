package engine.io;

import engine.graphics.Material;
import engine.graphics.mesh.Mesh3f;
import engine.graphics.mesh.MeshBuilder;
import engine.graphics.vertex.Vertex3f;
import engine.math.Vector2f;
import engine.math.Vector3f;
import org.lwjgl.assimp.*;

public class MeshLoader {

    public static Mesh3f loadModel(String filePath, Material material) {
        AIScene scene = Assimp.aiImportFile("resources" + filePath, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate);

        if (scene == null) {
            System.err.println("Couldn't load model at " + filePath);
            return MeshBuilder.Cube(1.0f, new Material("/textures/test.png"));
        }

        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));
        int vertexCount = mesh.mNumVertices();

        AIVector3D.Buffer vertices = mesh.mVertices();
        AIVector3D.Buffer normals = mesh.mNormals();

        Vertex3f[] vertex3fList = new Vertex3f[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            AIVector3D vertex = vertices.get(i);
            Vector3f meshVertex = new Vector3f(vertex.x(), vertex.y(), vertex.z());

            AIVector3D normal = normals.get(i);
            Vector3f meshNormal = new Vector3f(normal.x(), normal.y(), normal.z()); // TODO: turn normals into lighting

            Vector2f meshTextureCoord = new Vector2f(0, 0);
            if (mesh.mNumUVComponents().get(0) != 0) {
                AIVector3D texture = mesh.mTextureCoords(0).get(i);
                meshTextureCoord.setX(texture.x());
                meshTextureCoord.setY(texture.y());
            }

            vertex3fList[i] = new Vertex3f(meshVertex, meshNormal, meshTextureCoord);
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

        return new Mesh3f(vertex3fList, indicesList, material);
    }

}
