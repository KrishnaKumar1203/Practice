def noofaccurance(s):
    char_count_map = {}
    for c in s:
        char_count_map[c] = char_count_map.get(c, 0) + 1

    result = []
    for key, value in char_count_map.items():
        result.append(f"{key}={value}")
    return " ".join(result)

if __name__ == "__main__":
    print(noofaccurance("KrishnaKumar"))  # Output: k=2 r=2 ... (no of occurrence of each character)