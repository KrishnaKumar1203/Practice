public class MainClass
{
    public static void Main(string[] args)
    {
        string s = " I love javaa codingggg";
        string[] c = s.Split(" ");
        Dictionary<string, int> map = new Dictionary<string, int>();

        foreach (string r in c)
        {
            map[r] = r.Length;
        }

        List<int> lengths = new List<int>(map.Values);
        lengths.Sort();
        int maxLength = lengths[lengths.Count - 1];

        foreach (var entry in map)
        {
            if (entry.Value == maxLength)
            {
                Console.WriteLine($"Largest word is: {entry.Key} with length {maxLength}");
            }
        }
    }
}

