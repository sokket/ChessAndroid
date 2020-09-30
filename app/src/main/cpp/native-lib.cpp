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

    jboolean isCopy = true;
    const char *addr = env->GetStringUTFChars(server_address, &isCopy);

    struct addrinfo hints{}, *serv, *rp;
    bzero(&hints, sizeof(hints));
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_protocol = IPPROTO_TCP;

    int ret = getaddrinfo(addr, std::to_string(port).c_str(), &hints, &serv);
    if (ret != 0) {
        return false;
    }

    for (rp = serv; rp != nullptr; rp = rp->ai_next) {
        sock_fd = socket(rp->ai_family, rp->ai_socktype,
                         rp->ai_protocol);
        if (sock_fd == -1)
            continue;

        if (connect(sock_fd, rp->ai_addr, rp->ai_addrlen) != -1)
            break;                  /* Success */

        close(sock_fd);
    }

    if (rp == nullptr)
        return false;

    freeaddrinfo(serv);

    std::string proto = "CHESS_PROTO/1.0";
    int err = send_all(sock_fd, proto.c_str(), proto.length());

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