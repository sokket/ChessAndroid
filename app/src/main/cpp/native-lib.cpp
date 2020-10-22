#include <jni.h>
#include <string>
#include <sys/socket.h>
#include <netdb.h>
#include <strings.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <unistd.h>
#include <android/log.h>

#define PKG_CREATE_ROOM 0
#define PKG_JOIN_ROOM 1
#define PKG_MOVE 2
#define PKG_CASTLING 3
#define PKG_EN_PASSANT 4
#define PKG_CHAT_MSG 100
#define PKG_STATUS 200

#define PKG_CLIENT_ROOM_FULL 1
#define PKG_CLIENT_MOVE 2
#define PKG_CLIENT_CASTLING 3
#define PKG_CLIENT_EN_PASSANT 4
#define PKG_CLIENT_ROOM_NOT_FOUND 5
#define PKG_CLIENT_JOINED 6
#define PKG_CLIENT_CHAT_MSG 100
#define PKG_CLIENT_STATUS 200

int sock_fd = -1;

void disconnect() {
    if (sock_fd != -1) {
        shutdown(sock_fd, SHUT_RDWR);
        sock_fd = -1;
    }
}

typedef struct Position {
    char xO;
    char yO;
    char xN;
    char yN;
} Position;

int recv_all(int sock, void *data, int len) {
    auto *data_ptr = (unsigned char *) data;
    int num_recv;

    while (len > 0) {
        num_recv = recv(sock, data_ptr, len, 0);
        if (num_recv < 1)
            return 0;
        data_ptr += num_recv;
        len -= num_recv;
    }

    return 1;
}

int send_all(int sock, const void *data, unsigned int len) {
    auto *data_ptr = (unsigned char *) data;
    int num_sent;

    while (len > 0) {
        num_sent = send(sock, data_ptr, len, 0);
        if (num_sent < 1)
            return 0;
        data_ptr += num_sent;
        len -= num_sent;
    }

    return 1;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_chess_net_ChessClient_join(JNIEnv *env, jobject thiz, jstring key) {
    char type = 1;
    if (write(sock_fd, &type, 1) < 1)
        return false;

    char key_str[7];
    jboolean isCopy = false;
    strncpy(key_str, env->GetStringUTFChars(key, &isCopy), 7);

    if (send_all(sock_fd, key_str, 7)) {
        unsigned char status;
        return
            recv_all(sock_fd, &status, sizeof(char))
            ? status == PKG_CLIENT_JOINED
            : false;
    } else
        return false;
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_example_chess_net_ChessClient_newRoom(JNIEnv *env, jobject thiz) {
    char type = 0;
    if (!send_all(sock_fd, &type, 1))
        return env->NewStringUTF("ERROR");

    unsigned char s_type;
    if (!recv_all(sock_fd, &s_type, sizeof(char)))
        return env->NewStringUTF("ERROR");

    char code[8];
    if (!recv_all(sock_fd, &code, 7))
        return env->NewStringUTF("ERROR");

    code[7] = '\0';
    return env->NewStringUTF(code);
}


extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_chess_net_ChessClient_connect(JNIEnv *env, jobject thiz,
                                               jstring server_address, jint port) {
    disconnect();

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
    return send_all(sock_fd, proto.c_str(), proto.length());
}

typedef struct enPassant {
    unsigned char x;
    unsigned char y;
} EnPassant;

extern "C" JNIEXPORT void JNICALL
Java_com_example_chess_net_ChessClient_streamEvents(JNIEnv *env, jobject thiz,
                                                    jobject event_listener) {

    jmethodID method = env->GetMethodID(
            env->GetObjectClass(event_listener),
            "onEvent",
            "(Ljava/lang/Object;)V"
    );

    unsigned char type;
    while (recv_all(sock_fd, &type, sizeof(char)))
        if (type == PKG_CLIENT_MOVE) {
            Position position;
            if (!recv_all(sock_fd, &position, sizeof(position)))
                break;

            jint x1 = (unsigned char) position.xO;
            jint y1 = (unsigned char) position.yO;
            jint x2 = (unsigned char) position.xN;
            jint y2 = (unsigned char) position.yN;

            jclass cls = env->FindClass("com/example/chess/net/Move");
            jmethodID constructor = env->GetMethodID(cls, "<init>", "(IIII)V");
            jobject object = env->NewObject(cls, constructor, x1, y1, x2, y2);
            env->CallVoidMethod(event_listener, method, object);
        } else if (type == PKG_CLIENT_ROOM_FULL) {
            jclass cls = env->FindClass("com/example/chess/net/ServiceMessage");
            jfieldID field = env->GetStaticFieldID(cls, "ROOM_FULL", "Lcom/example/chess/net/ServiceMessage;");
            jobject msg = env->GetStaticObjectField(cls, field);
            env->CallVoidMethod(event_listener, method, msg);
        } else if (type == PKG_CLIENT_CASTLING) {
            char long_castling;
            if (!recv_all(sock_fd, &long_castling, sizeof(long_castling)))
                break;

            jclass cls = env->FindClass("com/example/chess/net/Castling");
            jmethodID constructor = env->GetMethodID(cls, "<init>", "(Z)V");
            jobject object = env->NewObject(cls, constructor, long_castling);
            env->CallVoidMethod(event_listener, method, object);
        } else if (type == PKG_CLIENT_EN_PASSANT) {
            EnPassant enPassant;
            if (!recv_all(sock_fd, &enPassant, sizeof(enPassant)))
                break;

            jint x = enPassant.x;
            jint y = enPassant.y;

            jclass cls = env->FindClass("com/example/chess/net/EnPassant");
            jmethodID constructor = env->GetMethodID(cls, "<init>", "(II)V");
            jobject object = env->NewObject(cls, constructor, x, y);
            env->CallVoidMethod(event_listener, method, object);
        } else if (type == PKG_CLIENT_CHAT_MSG) {
            char *message = nullptr;
            char next;
            int i = 0;
            do {
                if (message == nullptr)
                    message = static_cast<char *>(malloc(sizeof(char)));
                else
                    message = static_cast<char *>(realloc(message, i + 1));
                recv_all(sock_fd, &next, 1);
                message[i] = next;
                i++;
            } while (next != 0);

            jstring str = env->NewStringUTF(message);
            free(message);

            jclass cls = env->FindClass("com/example/chess/net/Message");
            jmethodID constructor = env->GetMethodID(cls, "<init>", "(Ljava/lang/String;)V");
            jobject object = env->NewObject(cls, constructor, str);
            env->CallVoidMethod(event_listener, method, object);
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

extern "C" JNIEXPORT void JNICALL
Java_com_example_chess_net_ChessClient_sendMessage(JNIEnv *env, jobject thiz, jstring text) {
    jboolean isCopy = false;
    const char *str = env->GetStringUTFChars(text, &isCopy);
    unsigned char pkg_type = 100;
    if (!send_all(sock_fd, &pkg_type, sizeof(char)))
        return;

    send_all(sock_fd, str, strlen(str) + 1);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_chess_net_ChessClient_enPassant(JNIEnv *env, jobject thiz, jint x, jint y) {
    unsigned char type = PKG_EN_PASSANT;
    if (!send_all(sock_fd, &type, sizeof(char)))
        return;

    EnPassant enPassant = EnPassant {
        static_cast<unsigned char>(x),
        static_cast<unsigned char>(y)
    };

    send_all(sock_fd, &enPassant, sizeof(enPassant));
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_chess_net_ChessClient_castling(JNIEnv *env, jobject thiz, jboolean long_castling) {
    unsigned char type = PKG_CASTLING;
    if (!send_all(sock_fd, &type, sizeof(char)))
        return;

    unsigned char c_long_castling = long_castling;

    send_all(sock_fd, &c_long_castling, sizeof(c_long_castling));
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_chess_net_ChessClient_disconnect(JNIEnv *env, jobject thiz) {
    disconnect();
}