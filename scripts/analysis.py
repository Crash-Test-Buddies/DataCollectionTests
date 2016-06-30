import argparse
import re
import io


def main():
    parser = argparse.ArgumentParser(description="Analyze Wifi Direct tests")
    parser.add_argument('file', metavar='file', type=str, nargs=1, help="The file to analyze")
    arguments = parser.parse_args()
    analyze(arguments.file[0])

def analyze(file_name):
    f = open(file_name)
    data = collect_data(f)
    with open("output.csv", "w") as output:
        output.write("step,start,end\n")
        for key, val in data.items():
            print("STEP: " + key)
            print("iterations: " + str(len(val)))

            times = list(map(lambda time: time[1] - time[0], val))
            print("min: " + str(min(times)))
            print("max: " + str(max(times)))
            print("avg: " + str(sum(times) / len(times)))

            sorted_times = sorted(times)
            print("med: " + str(sorted_times[int(len(sorted_times)/2)]))

            for timings in val:
                output.write(key + "," + str(timings[0]) + "," + str(timings[1]) + "\n")

def collect_data(open_file):
    regex = r"STEP: (.*)"
    results = {}

    line = open_file.readline()
    while line:
        step = re.findall(regex, line)[0]
        if step not in results:
            results[step] = []
        start = int(open_file.readline())
        end = int(open_file.readline())
        results[step].append((start, end))
        line = open_file.readline()

    return results


if __name__ == "__main__":
    main()
