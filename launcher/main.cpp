#include <filesystem>
#include <iostream>
#include <string>

int main() {
    std::string jre_path = R"(\jre\bin\java.exe)";
    std::string jre_args = R"(--enable-preview)";
    std::string jar_file = R"(sequencer.jar)";

    std::cout << "Launching Sequencer" << std::endl;

    const std::filesystem::path current_path = std::filesystem::current_path();

    std::string command = current_path.string() + jre_path + " " + jre_args + " " + jar_file;
    std::cout << "Running command: " << command << std::endl;
    system(command.c_str());

    return 0;
}
