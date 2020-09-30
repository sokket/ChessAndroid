#include <jni.h>
#include <string>
#include <sys/socket.h>
#include <netdb.h>
#include <strings.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <unistd.h>
#include <android/log.h>

int sock_fd;

typedef struct Position {
    char xO;
    char yO;
    char xN;
    char yN;
} Position;

int send_all(int sock, const void *data, int len) {
    auto *data_ptr = (unsigned char *) data;
    int num_sent;

    while (len > 0) {
        num_sent = send(sock, data_ptr, len, 0);
        if (num_sent < 1)
            return -1;
        data_ptr += num_sent;
        len -= num_sent;
    }

    return 0;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_chess_net_ChessClient_join(JNIEnv *env, jobject thiz, jstring key) {
    char type = 1;
    if (write(sock_fd, &type, 1) < 1)
        return false;

    char key_str[7];
    jboolean isCopy = false;
    strncpy(key_str, env->GetStringUTFChars(key, &isCopy), 7);
    return write(sock_fd, key_str, 7) >= 1;
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_example_chess_net_ChessClient_newRoom(JNIEnv *env, jobject thiz) {
    char type = 0;
    if (write(sock_fd, &type, 1) < 1) {
        return env->NewStringUTF("ERROR");
    }
    char code[8];
    if (recv(sock_fd, &code, 7, MSG_WAITALL) < 7) {
        return env->NewStringUTF("ERROR");
    }
    code[7] = '\0';
    return env->NewStringUTF(code);
}


extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_chess_net_ChessClient_connect(JNIEnv *env, jobject thiz,
                                               jstring server_address, jint port) {

    struct sockaddr_in server{};
    bzero(&server, sizeof(server));
    server.sin_family = AF_INET;
    server.sin_port = htons(port);
    jboolean isCopy = true;
    const char *addr = env->GetStringUTFChars(server_address, &isCopy);
    inet_pton(AF_INET, addr, &server.sin_addr);

    sock_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (sock_fd == -1)
        return false;

    int err = connect(sock_fd, (const struct sockaddr *) &server, sizeof(server));
    if (err != -1) {
        std::string proto_str = "CHESS_PROTO/1.0";
        const char *proto = proto_str.c_str();
        err = send_all(sock_fd, proto, strlen(proto));
    }

    return err != -1;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_chess_net_ChessClient_streamMoves(JNIEnv *env, jobject thiz,
                                                   jobject move_listener) {
    for (;;) {
        Position position;
        if (read(sock_fd, &position, sizeof(position)) < 1)
            break;

        jint x1 = (unsigned char) position.xO;
        jint y1 = (unsigned char) position.yO;
        jint x2 = (unsigned char) position.xN;
        jint y2 = (unsigned char) position.yN;
        env->CallVoidMethod(move_listener,
                            env->GetMethodID(env->GetObjectClass(move_listener), "onMakeMove",
                                             "(IIII)V"), x1, y1, x2, y2);
    }

    close(sock_fd);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_chess_net_ChessClient_move(JNIEnv *env, jobject thiz, jint x_old, jint y_old,
                                            jint x_new, jint y_new) {
    int type = 2;
    write(sock_fd, &type, 1);

    Position position;
    position.xO = x_old;
    position.yO = y_old;
    position.xN = x_new;
    position.yN = y_new;
    int wr = write(sock_fd, &position, sizeof(position));
    if (wr < 1)
        __android_log_print(ANDROID_LOG_ERROR, "ERR", "WR error");
}