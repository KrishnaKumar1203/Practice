def middle_value():
    values = []
    print("Enter values one by one. Type 'done' to finish:")
    while True:
        inp = input()
        if inp.lower() == 'done':
            break
        try:
            values.append(int(inp))
        except ValueError:
            print("Invalid input. Please enter an integer or 'done' to finish:")

    n = len(values)
    if n == 0:
        print("No values entered.")
        return

    centralpoint = n // 2
    sumleft = sum(values[:centralpoint])
    sumright = sum(values[centralpoint+1:])
    central2 = values[centralpoint] if n > 0 else None

    print("Sum of left side:", sumleft)
    print("Sum of right side:", sumright)
    if sumleft == sumright:
        print("Central point is:", central2)
    else:
        print("Central point is not found")


def interview():
    # Merge and sort two lists, remove zeros
    A = [6, 2, 4, 0, 0, 0, 0]
    B = [3, 1, 5]
    merged = [x for x in (A + B) if x != 0]
    merged.sort()
    print("Merged and Sorted List:", merged)
    merged.reverse()
    print("Reversed List:", merged)

    # Find index of an element in a list
    arr = [8, 7, 6, 7, 6, 5, 4, 3, 2, 3, 4, 3]
    x = 3
    try:
        index = arr.index(x)
        print(f"Element {x} found at index {index}")
    except ValueError:
        print(f"Element {x} not found in the array.")


def interview2():
    a = [6, 4, 3, 2, 9, 8]
    b = len(a)
    i = 0
    while i < b - 1:
        print(f"i located at = {i}")
        a[i], a[i+1] = a[i+1], a[i]
        i += 2
    for val in a:
        print(val)


def interview1():
    a = [1, 1, 0, 1, 0]
    b = len(a)
    d = [None] * b
    c = 0
    end = b - 1
    for i in range(b):
        if a[i] == 0:
            d[c] = 0
            c += 1
        else:
            d[end] = 1
            end -= 1
    for val in d:
        print(val)


if __name__ == "__main__":
    # Uncomment the following line to use middle_value interactively
    # middle_value()
    interview()
    interview1()
    interview2()