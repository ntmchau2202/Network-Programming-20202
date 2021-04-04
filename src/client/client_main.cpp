#include <iostream>
#include <sstream>
#include <string>

#include "client.hpp"

using namespace std;

int main(int argc, char *argv[]){
    Client *client_instance = NULL;
    try {
        client_instance = Client::StartSession();
    } catch (int e) {
        cout << "Error when initialzing client.\nClient error code: " << e << "\nError details: " + GetLastError() << endl;
        exit(-1);
    }
    
    string input_string;
    
    while(true){
        char *buffer = (char*)calloc(MAX_LEN_BUFFER, sizeof(char));
        client_instance->ReceiveMsg(buffer, &(client_instance->client_socket));
        cout << "Message received: " << string(buffer) << endl;
        input_string.clear();
        cout << "Enter a message to send: ";
        cin >> input_string;
        client_instance->SendMsg(input_string, &(client_instance->client_socket));
    }
}