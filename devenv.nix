{
  pkgs,
  lib,
  config,
  inputs,
  ...
}: {
  # https://devenv.sh/basics/
  # env.GREET = "devenv";

  # https://devenv.sh/packages/
  packages = with pkgs; [
    javaPackages.compiler.temurin-bin.jdk-25
    gradle_9
    docker
  ];

  # https://devenv.sh/languages/
  # languages.rust.enable = true;

  # https://devenv.sh/processes/
  # processes.dev.exec = "${lib.getExe pkgs.watchexec} -n -- ls -la";

  # https://devenv.sh/services/
  # services.postgres.enable = true;

  # https://devenv.sh/scripts/
  # scripts.hello.exec = ''
  #   echo hello from $GREET
  # '';

  # https://devenv.sh/tasks/
  tasks = {
    "ss:test".exec = "(cd ssbackend && ./gradlew test)";
    "ss:server".exec = "(cd ssbackend && ./gradlew bootRun)";
    "ss:mobile".exec = "(cd ssmobile && ./gradlew run)"; # todo: start android emulator
  };

  # See full reference at https://devenv.sh/reference/options/
  android.enable = true;
}
