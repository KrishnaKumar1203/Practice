def main():
    name = "Krishna Kumar"
    n = len(name)
    process = set()
    char_map = {}

    print("The length of the array is", n)

    # Build the map (character -> last index)
    for c in range(n):
        char_map[name[c]] = c
    print("The map is", char_map)

    for i in range(n):
        if name[i] in process:
            continue
        count = 0
        for j in range(n):
            if name[i] == name[j]:
                count += 1
                process.add(name[i])
        if count > 1:
            print(f"The character '{name[i]}' is duplicate and present {count} times")

if __name__ == "__main__":
    main()