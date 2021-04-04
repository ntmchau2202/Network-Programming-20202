cmake_minimum_required(VERSION 3.13)

project(network_programming)

#names
set(SERVER_MAIN "server_main")
set(CLIENT_MAIN "client_main")

# find_library(JSON jsoncpp hints "C:\\Users\\Admin\\vcpkg\\packages\\jsoncpp_x64-windows\\lib")

#add files to executables
add_executable(
    ${SERVER_MAIN}
    server/server_main.cpp
    
    server/server.hpp
    server/server.cpp

    socket/socket.hpp
    socket/socket.cpp
)

add_executable(
    ${CLIENT_MAIN}
    client/client_main.cpp

    client/client.hpp
    client/client.cpp  

    socket/socket.hpp
    socket/socket.cpp
)

# add_executable(
#     ${TESTER}
#     test/test.cpp

#     structures/protocol.hpp
#     structures/protocol.cpp
#     structures/course.hpp
#     structures/course.cpp
# )

#add libraries for winsock2
target_link_libraries(${SERVER_MAIN} "ws2_32.lib" ${JSON})
target_link_libraries(${CLIENT_MAIN} "ws2_32.lib" ${JSON})
#target_link_libraries(${TESTER} "ws2_32.lib" ${JSON})
