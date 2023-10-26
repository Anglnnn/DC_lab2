package main

import (
	"fmt"
	"sync"
	"time"
)

type Monk struct {
	Name string
	Qi   int
}

func compareMonks(monk1, monk2 Monk) Monk {
	if monk1.Qi > monk2.Qi {
		return monk1
	} else {
		return monk2
	}
}

func findWinner(monks []Monk, left, right int) Monk {
	if left == right {
		return monks[left]
	}

	mid := (left + right) / 2

	winner1 := findWinner(monks, left, mid)
	winner2 := findWinner(monks, mid+1, right)

	return compareMonks(winner1, winner2)
}

func startNewThread(monks []Monk, left, right int, winner chan Monk) {
	go func() {
		winner <- findWinner(monks, left, right)
	}()
}

func findTournamentWinner(monks []Monk) Monk {
	winner := make(chan Monk)

	wg := sync.WaitGroup{}

	for i := 0; i < len(monks)-1; i += 2 {
		wg.Add(1)
		startNewThread(monks, i, i+1, winner)
	}

	select {
	case winner := <-winner:
		return winner
	case <-time.After(100 * time.Millisecond):
		return <-winner
	}
}

func main() {
	monks := []Monk{
		Monk{Name: "Ivanov", Qi: 100},
		Monk{Name: "Petrov", Qi: 90},
		Monk{Name: "Nechiporchuk", Qi: 80},
	}

	winner := findTournamentWinner(monks)

	fmt.Println("The winner is:", winner.Name)
}
